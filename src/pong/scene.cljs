(ns pong.scene
  (:require cljsjs.three
            [pong.defs :refer [canvas-size]]
            [pong.game-objects :refer [ball paddle-1 paddle-2 plane]]
            [pong.utils :refer [log-position]]))

(def THREE js/THREE)

;; Camera

(def camera (THREE.PerspectiveCamera. 50 (/ (:width canvas-size) (:height canvas-size)) 0.1 10000))
(set! (.-z (.-position camera)) 320)

;; Lights

(def pointLight (THREE.PointLight. 0xF8D898))
(set! (-> pointLight .-position .-x) -1000)
(set! (-> pointLight .-position .-y) 0)
(set! (-> pointLight .-position .-z) 1000)
(set! (-> pointLight .-intensity) 2.9)
(set! (-> pointLight .-distance) 10000)

;; Scene

(def scene (THREE.Scene.))
(.add scene camera)
(.add scene ball)
(.add scene plane)
(.add scene paddle-1)
(.add scene paddle-2)
(.add scene pointLight)

;; Renderer

(def renderer (THREE.WebGLRenderer.))

;; Make objects movable!

(defprotocol IMoveableObject
  "Defines movement capabilities (changes in 'position') for ~THREE.js~ objects"
  (move! [this pos] "Move this object to a certain position")
  (get-position [this] "Returns this object's current positions")
  (console-position [this]))

(extend-type THREE.Mesh
  IMoveableObject
  (move! [this {:keys [x y]}]
    (set! (-> this .-position .-x) x)
    (set! (-> this .-position .-y) y))
  (get-position [this]
    {:x (-> this .-position .-x)
     :y (-> this .-position .-y)
     :z (-> this .-position .-z)})
  (console-position [this]
    (-> this get-position log-position)))

(defn update-object-positions! [elements]
  (move! paddle-1 (-> elements :paddle-1 :position))
  (move! paddle-2 (-> elements :paddle-2 :position))
  (move! ball (-> elements :ball :position)))
