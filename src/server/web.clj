(ns server.web
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.util.response :refer [content-type file-response]]))

(def static-root (if (= (System/getenv "DEV_ENV") "production") "release" "target"))

(defn root-handler [req]
  (-> (file-response "index.html" {:root static-root})
      (content-type "text/html")))

(def app (-> root-handler
             (wrap-file static-root {:index-files? false})
             (wrap-content-type)))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") 3000))]
    (run-server app {:port port})
    (log/info "Started server on localhost:" port)))
