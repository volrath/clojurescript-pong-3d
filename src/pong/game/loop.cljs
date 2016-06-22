(ns pong.game.loop
  (:require [pong.frontend.scene :refer [camera renderer scene update-scene!]]
            [pong.game.logic :refer [match-score update-movement] :as logic]))

(defn main-loop
  ([] (main-loop logic/elements))
  ([{:keys [ball paddle-1 paddle-2] :as elements} & more]
   ;; (.update stats)
   (let [elements (update-movement elements)]
     (update-scene! elements)
     (.render renderer scene camera)
     (match-score (:ball elements))
     (js/requestAnimationFrame (partial main-loop elements)))))
