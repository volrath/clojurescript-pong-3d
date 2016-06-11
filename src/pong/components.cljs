(ns pong.components
  (:require cljsjs.react-slider
            [pong.defs :refer [up-to-wins]]
            [pong.logic :refer [game-over? player-score scores opponent-reflexes]]
            [rum.core :as rum]))

(defn declare-winner [p1-score p2-score]
  (let [winner-text {p1-score "You win!" p2-score "CPU wins!"}]
    [:h1#scores (get winner-text (max p1-score p2-score))]))

(defn scoring [player-1 player-2]
  [:h1#scores
   [:span "You"] (str player-1 " - " player-2) [:span "CPU"]])

(rum/defc score-board < rum/reactive []
  (let [scores (rum/react scores)
        p1-score (player-score :player-1 scores)
        p2-score (player-score :player-2 scores)]
    [:div#score-board
     (if (game-over?)
       (declare-winner p1-score p2-score)
       (scoring p1-score p2-score))]))

(def slider
  (rum/element
   js/ReactSlider
   {}
   #js {:defaultValue @opponent-reflexes
        :min 1
        :max 9
        :withBars true
        :onChange #(reset! opponent-reflexes %)}))

(rum/defc reflexes-slider < rum/reactive []
  [:div#reflexes-slider
   (str "Opponent's Difficulty: " (rum/react opponent-reflexes))
   slider])


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
   (score-board)
   [:div#container]
   (reflexes-slider)
   [:h1#title "- 3D Pong -"]
   [:h2#winner-board (if (game-over?)
                       "Refresh to play again"
                       (str "First to " up-to-wins " wins!"))]
   [:h3#controls
    "A - move left" [:br]
    "D - move right" [:br]
    "SPC - Pause"]
   (footer)])

