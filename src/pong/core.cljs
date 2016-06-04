(ns pong.core
  (:require libs.stats
            [pong.controls :refer [controls-listen paddle-movement]]
            [pong.defs :refer [canvas-size]]
            [pong.game-objects :refer [paddle-x paddle-depth]]
            [pong.logic :refer [update-logic]]
            [pong.scene :refer [camera renderer scene update-object-positions!]]
            [pong.utils :refer [log-position]]))

(enable-console-print!)

(def stats (js/Stats))

(def initial-elements
  {:ball {:x 0 :y 0 :z 0}
   :paddle-1 {:position {:x (paddle-x :left) :y 0 :z paddle-depth}
              :side :left}
   :paddle-2 {:position {:x (paddle-x :right) :y 0 :z paddle-depth}
              :side :right}})

(defn clean-up-node [node]
  (while (.-firstChild node) (.removeChild node (.-firstChild node))))

(defn setup-container []
  (let [container (.getElementById js/document "container")]
    (clean-up-node container)
    (.appendChild container (.-dom stats))
    (.appendChild container (.-domElement renderer))))

(defn main-loop [{:keys [ball paddle-1 paddle-2] :as elements}]
  (.update stats)
  (let [elements (update-logic elements)]
    (update-object-positions! elements)
    (.render renderer scene camera)
    (js/requestAnimationFrame (partial main-loop elements))))

(defn init []
  (println "We're up and running!")
  (.setSize renderer (:width canvas-size) (:height canvas-size))
  (setup-container)
  (controls-listen)
  (main-loop initial-elements))


(init)
