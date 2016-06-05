(ns pong.core
  (:require libs.stats
            [pong.components :refer [game-container]]
            [pong.controls :refer [controls-listen paddle-movement]]
            [pong.defs :refer [canvas-size]]
            [pong.logic :refer [update-movement] :as logic]
            [pong.scene :refer [camera renderer scene update-object-positions!]]
            [rum.core :as rum]))

(enable-console-print!)

(def stats (js/Stats))

(defn main-loop [{:keys [ball paddle-1 paddle-2] :as elements}]
  (.update stats)
  (let [elements (update-movement elements)]
    (update-object-positions! elements)
    (.render renderer scene camera)
    (js/requestAnimationFrame (partial main-loop elements))))

(defn init []
  (println "We're up and running!")
  ;; Set up
  (controls-listen)
  (.setSize renderer (:width canvas-size) (:height canvas-size))
  (rum/mount (game-container renderer stats) (.getElementById js/document "main"))
  ;; Run
  (main-loop logic/elements))


(init)
