(ns pong.controls
  (:require [goog.events :as events])
  (:import [goog.events.EventType]
           [goog.events KeyHandler]))

(def paddle-movement (atom {:left nil :right nil}))
(def code-key-map
  {87 [:left :up]
   65 [:left :up]
   83 [:left :down]
   68 [:left :down]
   37 [:right :up]
   38 [:right :up]
   39 [:right :down]
   40 [:right :down]})

(defn- alter-movement [side direction]
  (swap! paddle-movement assoc side direction))

(defn- key-pressed [event]
  (if-let [[side direction] (->> event .-keyCode (get code-key-map))]
    (alter-movement side direction)))

(defn- key-released [event]
  (if-let [side (->> event .-keyCode (get code-key-map) first)]
    (alter-movement side nil)))

(defn controls-listen []
  (events/listen (.-body js/document)
                 (.-KEYDOWN events/EventType)
                 key-pressed)
  (events/listen (.-body js/document)
                 (.-KEYUP events/EventType)
                 key-released))
