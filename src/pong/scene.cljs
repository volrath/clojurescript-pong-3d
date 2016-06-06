(ns pong.scene
  (:require cljsjs.three
            [pong.defs :refer [canvas-size]]
            [pong.game-objects :refer [ball ground paddle-1 paddle-2 pillars plane table]]
            [pong.utils :refer [log-position]]))

(def THREE js/THREE)

;;
;; Make objects movable!
;; -----------------------------------------------------------------------------

(defprotocol IMoveableObject
  "Defines movement capabilities (changes in 'position') for ~THREE.js~ objects"
  (move! [this pos] "Move this object to a certain position")
  (rotate! [this pos] "Rotate this object to a certain position")
  (get-position [this] "Returns this object's current positions")
  (console-position [this]))

(extend-type THREE.Object3D
  IMoveableObject
  (move! [this {:keys [x y z]}]
    (set! (-> this .-position .-x) x)
    (set! (-> this .-position .-y) y)
    (if z
      (set! (-> this .-position .-z) z)))
  (rotate! [this {:keys [x y z]}]
    (set! (-> this .-rotation .-x) x)
    (set! (-> this .-rotation .-y) y)
    (set! (-> this .-rotation .-z) z))
  (get-position [this]
    {:x (-> this .-position .-x)
     :y (-> this .-position .-y)
     :z (-> this .-position .-z)})
  (console-position [this]
    (-> this get-position log-position)))

;;
;; Scene objects
;; -----------------------------------------------------------------------------

;; Camera

(def camera (THREE.PerspectiveCamera. 50 (/ (:width canvas-size) (:height canvas-size)) 0.1 10000))

;; Lights

(def point-light (THREE.PointLight. 0xF8D898))
(set! (-> point-light .-intensity) 2.9)
(set! (-> point-light .-distance) 10000)

(def spot-light (THREE.SpotLight. 0xF8D898))
(set! (-> spot-light .-intensity) 1.5)

;; Scene

(def scene (THREE.Scene.))
(.add scene camera)
(.add scene plane)
(.add scene table)
(.add scene ball)
(.add scene paddle-1)
(.add scene paddle-2)
(.add scene ground)
(.add scene point-light)
(.add scene spot-light)

;; Set shadows
(set! (.-receiveShadow plane) true)
(set! (.-receiveShadow table) true)
(set! (.-receiveShadow ball) true)
(set! (.-castShadow ball) true)
(set! (.-receiveShadow paddle-1) true)
(set! (.-castShadow paddle-1) true)
(set! (.-receiveShadow paddle-2) true)
(set! (.-castShadow paddle-2) true)
(set! (.-receiveShadow ground) true)
(set! (.-castShadow spot-light) true)

;; Move static things around...

(def position
  (fn
    ([x y] {:x x :y y})
    ([x y z] {:x x :y y :z z})))
(move! camera (position 0 0 320))
(move! table (position 0 0 -51))
(move! ground (position 0 0 -132))
(move! point-light (position -1000 0 1000))
(move! spot-light (position 0 0 460))

;; Pillars
(dotimes [i (count pillars)]
  (let [pillar (nth pillars i)]
    (.add scene pillar)
    (set! (.-receiveShadow pillar) true)
    (set! (.-castShadow pillar) true)
    (move! pillar (position (+ -50 (* 100 (mod i 5))) ((if (< i 5) + -) 230) -30))))

;; Renderer

(def renderer (THREE.WebGLRenderer.))
(set! (-> renderer .-shadowMap .-enabled) true)
(.setSize renderer (:width canvas-size) (:height canvas-size))


;;
;; Update helpers
;; -----------------------------------------------------------------------------

(defn -update-elements-positions! [elements]
  (move! paddle-1 (-> elements :paddle-1 :position))
  (move! paddle-2 (-> elements :paddle-2 :position))
  (move! ball (-> elements :ball :position)))

(defn -update-elements-reactions! [elements]
  elements)

(defn -update-camera!
  [{bx :x by :y}
   {px :x py :y pz :z}]
  (let [cy (-> camera .-position .-y)
        half-pi (/ js/Math.PI 180)]
    (move! spot-light (position (* bx 2) (* by 2)))
    (move! camera (position (- px 100)
                            (+ cy (* 0.05 (- py cy)))
                            (+ (* 0.04 (- px bx)) (+ 100 pz))))
    (rotate! camera (position (* -0.01 by half-pi)
                              (* -60 half-pi)
                              (* -90 half-pi)))))

(defn update-scene! [elements]
  (-update-camera! (-> elements :ball :position) (-> elements :paddle-1 :position))
  (-update-elements-positions! elements)
  (-update-elements-reactions! elements))
