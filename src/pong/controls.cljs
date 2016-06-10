(ns pong.controls
  (:require [goog.events :as events])
  (:import [goog.events.EventType]
           [goog.events KeyHandler]))

(def paddle-movement (atom {:left nil :right nil}))
(def paused? (atom false))
(def code-key-map
  {87 [:left :up]
   65 [:left :up]
   83 [:left :down]
   68 [:left :down]
   37 [:right :up]
   38 [:right :up]
   39 [:right :down]
   40 [:right :down]
   32 #(swap! paused? not)})

(defn- alter-movement [side direction]
  (swap! paddle-movement assoc side direction))

(defn- key-pressed [event]
  (if-let [action (->> event .-keyCode (get code-key-map))]
    (if (fn? action)
      (action)
      (alter-movement (first action) (second action)))))

(defn- key-released [event]
  (if-let [action (->> event .-keyCode (get code-key-map))]
    (if (not (fn? action))
      (let [side (first action)
            direction (second action)]
        (if (= direction (side @paddle-movement))
          (alter-movement side nil))))))

(defn controls-listen []
  (events/listen (.-body js/document)
                 (.-KEYDOWN events/EventType)
                 key-pressed)
  (events/listen (.-body js/document)
                 (.-KEYUP events/EventType)
                 key-released))
