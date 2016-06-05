(ns pong.core
  (:require libs.stats
            [pong.controls :refer [controls-listen paddle-movement]]
            [pong.defs :refer [canvas-size]]
            [pong.logic :refer [update-movement] :as logic]
            [pong.scene :refer [camera renderer scene update-object-positions!]]
            [pong.utils :refer [log-position]]))

(enable-console-print!)

(def stats (js/Stats))

(defn clean-up-node [node]
  (while (.-firstChild node) (.removeChild node (.-firstChild node))))

(defn setup-container []
  (let [container (.getElementById js/document "container")]
    (clean-up-node container)
    (.appendChild container (.-dom stats))
    (.appendChild container (.-domElement renderer))))

(defn main-loop [{:keys [ball paddle-1 paddle-2] :as elements}]
  (.update stats)
  (let [elements (update-movement elements)]
    (update-object-positions! elements)
    (.render renderer scene camera)
    (js/requestAnimationFrame (partial main-loop elements))))

(defn init []
  (println "We're up and running!")
  ;; Set up
  (.setSize renderer (:width canvas-size) (:height canvas-size))
  (setup-container)
  (controls-listen)
  ;; Run
  (main-loop logic/elements))


(init)
