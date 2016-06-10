(ns server.web
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes]]
            [compojure.route :refer [files not-found resources]]
            [org.httpkit.server :refer [run-server]]))

(def static-root (if (= (System/getenv "DEV_ENV") "production") "release" "target"))

(defroutes app
  (files "/" {:root static-root})
  (resources "/" {:root static-root})
  (not-found "Page not found"))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") 3000))]
    (run-server app {:port port})
    (log/info "Started server on localhost:" port)))
