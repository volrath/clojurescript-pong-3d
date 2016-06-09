(ns server.web
  (:gen-class)
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :refer [files not-found resources]]
            [org.httpkit.server :refer [run-server]]))

(defroutes app
  (files "/" {:root "target"})
  (resources "/" {:root "target"})
  (not-found "Page not found"))

(defn -main []
  (let [port 3000]
    (run-server app {:port port})
    (println "Started server on localhost:" port)))
