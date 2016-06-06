(ns pong.game-objects
  (:require cljsjs.three
            [pong.defs :refer [field-size]]))

(def ball-radius 5)
(def ball-geometry (js/THREE.SphereGeometry. ball-radius 6 6))  ; radius, segments, rings
(def ball-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0xD43001)))
(def ball (js/THREE.Mesh. ball-geometry ball-material))

(def plane-geometry (js/THREE.PlaneGeometry. (* 0.95 (:width field-size)) (:height field-size) 10 10))
(def plane-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x4BD121)))
(def plane (js/THREE.Mesh. plane-geometry plane-material))

(def table-geometry (js/THREE.CubeGeometry. (* 1.05 (:width field-size)) (* 1.03 (:height field-size)) 100 10 10 1))
(def table-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x111111)))
(def table (js/THREE.Mesh. table-geometry table-material))

(def ground-geometry (js/THREE.CubeGeometry. 1000 1000 3 1 1 1))
(def ground-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x888888)))
(def ground (js/THREE.Mesh. ground-geometry ground-material))

(def pillar-geometry (js/THREE.CubeGeometry. 30 30 300 1 1 1))
(def pillar-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x534d0d)))
(def pillars (repeatedly 10 #(js/THREE.Mesh. pillar-geometry pillar-material)))

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
