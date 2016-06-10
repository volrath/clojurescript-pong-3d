(ns pong.components
  (:require [pong.defs :refer [up-to-wins]]
            [pong.logic :refer [game-over? player-score scores]]
            [rum.core :as rum]))

(defn declare-winner [p1-score p2-score]
  (let [winner-text {p1-score "You win!" p2-score "CPU wins!"}]
    (get winner-text (max p1-score p2-score))))

(rum/defc score-board < rum/reactive []
  (let [scores (rum/react scores)
        p1-score (player-score :player-1 scores)
        p2-score (player-score :player-2 scores)]
    [:div#score-board
     [:h1#scores (if (game-over?)
                   (declare-winner p1-score p2-score)
                   (str p1-score " - " p2-score))]
     [:h1#title "- 3D Pong -"]
     [:h2#winner-board (if (game-over?)
                         "Refresh to play again"
                         (str "First to " up-to-wins " wins!"))]
     [:h3#controls "A - move left" [:br] "D - move right"]]))

(defn footer []
  [:footer
   [:a
    {:href "https://github.com/volrath/clojurescript-pong-3d/" :target "_blank"}
    "https://github.com/volrath/clojurescript-pong-3d/"]])

(def mount-scene
  {:did-mount (fn [state]
                (let [comp (:rum/react-component state)
                      main-node (js/ReactDOM.findDOMNode comp)
                      game-node (.querySelector main-node "#container")  ; coupled to `game-container` definition :/ TODO: DRY it
                      [renderer stats] (:rum/args state)]
                  (.appendChild main-node (.-dom stats))
                  (.appendChild game-node (.-domElement renderer))))})

(rum/defc game-container < mount-scene [renderer stats]
  [:div
   [:div#container]
   (score-board)
   (footer)])

