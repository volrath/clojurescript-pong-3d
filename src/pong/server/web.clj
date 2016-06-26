(ns pong.server.web
  (:gen-class)
  (:require [domkm.silk :as silk]
            [domkm.silk.serve :refer [request-map->URL POST GET]]
            [org.httpkit.server :refer [run-server]]
            [pong.server.ws :refer [ring-ajax-post ring-ajax-get-or-ws-handshake]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [content-type file-response]]))

(def static-root
  (if (= (System/getenv "DEV_ENV") "production")
    "release"
    "target"))

(defn method
  ([method-fn path]
   (merge (method-fn) {:path path}))
  ([method-fn path query]
   (merge (method-fn) {:path path :query query})))

(def routes (silk/routes [[:get-chsk (method GET ["chsk"])]
                          [:post-chsk (method POST ["chsk"])]]))

(defn root-handler [req]
  (-> (file-response "index.html" {:root static-root})
      (content-type "text/html")))

(defmulti get-handler identity)
(defmethod get-handler :get-chsk [_] ring-ajax-get-or-ws-handshake)
(defmethod get-handler :post-chsk [_] ring-ajax-post)
(defmethod get-handler :default [_] root-handler)

(defn ring-handler
  "Takes a routes data structure or instance of `domkm.silk/Routes` and a `get-handler` function.
  `get-handler` should take a route name and return a handler function for that route.
  Returns a Ring handler function that:
  * takes Ring request map
  * converts the request map to a URL
  * associates the request map into the URL
  * matches routes against the new URL
  * when a match is found, associates params into request and passes that to a route handler function"
  ([routes]
   (ring-handler routes identity))
  ([routes get-handler]
   (let [rtes (silk/routes routes)]
     (fn [req]
       (if-let [params (silk/match rtes (request-map->URL req))]
         ((-> params :domkm.silk/name get-handler)
          (merge-with merge req {:params params}))
         ((get-handler nil) req))))))

(def app (-> (ring-handler routes get-handler)
             (wrap-content-type)
             (wrap-file static-root {:index-files? false})
             (wrap-keyword-params)
             (wrap-params)
             (wrap-anti-forgery)
             (wrap-session)))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") 3000))]
    (run-server app {:port port})
    (log/info "Started server on localhost:" port)))
