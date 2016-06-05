(ns pong.game-objects
  (:require cljsjs.three
            [pong.defs :refer [field-size]]))

(def ball-geometry (js/THREE.SphereGeometry. 5 6 6))  ; radius, segments, rings
(def ball-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0xD43001)))
(def ball (js/THREE.Mesh. ball-geometry ball-material))

(def plane-geometry (js/THREE.PlaneGeometry. (* 0.95 (:width field-size)) (:height field-size) 10 10))
(def plane-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x4BD121)))
(def plane (js/THREE.Mesh. plane-geometry plane-material))

(def paddle-width 10)
(def paddle-height 30)
(def paddle-depth 10)
(def paddle-quality 1)
(def paddle-1-geometry (js/THREE.CubeGeometry. paddle-width paddle-height paddle-depth paddle-quality paddle-quality))
(def paddle-1-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x1B32C0)))
(def paddle-1 (js/THREE.Mesh. paddle-1-geometry paddle-1-material))
(def paddle-2-geometry (js/THREE.CubeGeometry. paddle-width paddle-height paddle-depth paddle-quality paddle-quality))
(def paddle-2-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0xFF4045)))
(def paddle-2 (js/THREE.Mesh. paddle-2-geometry paddle-2-material))

;; Should be in a logic module?
;; (set! (-> paddle-1 .-position .-x) (+ (- ) paddle-width))
;; (set! (-> paddle-2 .-position .-x) (- (/ (:width field-size) 2) paddle-width))
;; (set! (-> paddle-1 .-position .-z) paddle-depth)
;; (set! (-> paddle-2 .-position .-z) paddle-depth)
