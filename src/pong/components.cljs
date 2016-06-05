(ns pong.components
  (:require [pong.defs :refer [up-to-wins]]
            [pong.logic :refer [player-score scores]]
            [rum.core :as rum]))

(rum/defc -score-board < rum/reactive []
  (let [p1-score (player-score :player-1 (rum/react scores))
        p2-score (player-score :player-2 (rum/react scores))]
    [:div#score-board
     [:h1#scores (str p1-score " - " p2-score)]
     [:h1#title "- 3D Pong -"]
     [:h2#winner-board (str "First to " up-to-wins " wins!")]]))

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

