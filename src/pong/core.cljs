(ns pong.core
  (:require libs.stats
            [pong.components :refer [game-container]]
            [pong.controls :refer [controls-listen]]
            [pong.defs :refer [canvas-size]]
            [pong.logic :refer [match-score update-movement] :as logic]
            [pong.scene :refer [camera renderer scene update-scene!]]
            [rum.core :as rum]))

(enable-console-print!)

(def stats (js/Stats))

(defn main-loop [{:keys [ball paddle-1 paddle-2] :as elements}]
  (.update stats)
  (let [elements (update-movement elements)]
    (update-scene! elements)
    (.render renderer scene camera)
    (match-score (:ball elements))
    (js/requestAnimationFrame (partial main-loop elements))))

(defn init []
  (println "We're up and running!")
  ;; Set up
  (controls-listen)
  (rum/mount (game-container renderer stats) (.getElementById js/document "main"))
  ;; Run
  (main-loop logic/elements))


(init)
