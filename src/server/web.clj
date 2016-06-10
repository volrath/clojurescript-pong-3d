(ns server.web
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes]]
            [compojure.route :refer [files not-found resources]]
            [org.httpkit.server :refer [run-server]]))

(defroutes app
  (files "/" {:root "target"})
  (resources "/" {:root "target"})
  (not-found "Page not found"))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") 3000))]
    (run-server app {:port port})
    (log/info "Started server on localhost:" port)))
