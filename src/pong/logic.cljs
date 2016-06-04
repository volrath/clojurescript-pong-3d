(ns pong.logic
  (:require [pong.controls :refer [paddle-movement]]
            [pong.defs :refer [field-size]]
            [pong.game-objects :refer [paddle-speed]]))


(defn alert-invalid-move-for-paddle [{:keys [side] :as paddle}]
  ;; (update curr-position :z #(* (- 10 %) 0.2))
  (println "boundary reached" side)
  paddle)

(defn move-paddle [paddle direction]
  (let [op (if (= direction :up) + -)]
    (update-in paddle [:position :y] op paddle-speed)))

(defn paddle-within-boundaries [{:keys [y]}]
  (let [half-boundary (* (:height field-size) 0.45)]
    (and (< y half-boundary)
         (> y (- half-boundary)))))

(defn new-paddle-position [{:keys [side] :as paddle}]
  (if-let [direction (get @paddle-movement side)]
    (let [new-paddle (move-paddle paddle direction)]
      (if (paddle-within-boundaries (:position new-paddle))
        new-paddle
        (alert-invalid-move-for-paddle paddle)))
    paddle))

(defn update-logic [{:keys [paddle-1 paddle-2 ball]}]
  {:paddle-1 (new-paddle-position paddle-1)
   :paddle-2 (new-paddle-position paddle-2)
   :ball ball})
