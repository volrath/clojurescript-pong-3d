(ns pong.components
  (:require [pong.logic :refer [score]]
            [rum.core :as rum]))

(def p1-score (rum/cursor score [:player-1]))
(def p2-score (rum/cursor score [:player-2]))

(rum/defc -score-board < rum/reactive []
  [:div#score-board
   [:h1#scores (str (rum/react p1-score) " - " (rum/react p2-score))]
   [:h1#title "- 3D Pong -"]
   [:h2#winner-board "First to 7 wins!"]])

(def mount-scene
  {:did-mount (fn [state]
                (let [comp (:rum/react-component state)
                      main-node (js/ReactDOM.findDOMNode comp)
                      game-node (.querySelector main-node "#container")  ; couple to `game-container` definition :/ TODO: DRY it
                      [renderer stats] (:rum/args state)]
                  (.appendChild main-node (.-dom stats))
                  (.appendChild game-node (.-domElement renderer))))})

(rum/defc game-container < mount-scene [renderer stats]
  [:div
   [:div#container]
   (-score-board)])

