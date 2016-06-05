(ns pong.utils)

(defn log-position [{:keys [x y z]}]
  (.trace js/console (str "X: " x " Y: " y " Z: " z)))

;;
;; Vector math
;; -----------------------------------------------------------------------------

(defprotocol I3DVector
  (v+ [x y] "Vector addition")
  (v-scalar-* [v s] "Vector multiplication by scalar")
  (magnitude [v] "Vector's magnitude")
  (normalize [v] "Vector normalization"))

(defrecord Vector [x y z]
  I3DVector
  (v+ [{v1x :x v1y :y v1z :z} {v2x :x v2y :y v2z :z}]
    (Vector. (+ v1x v2x) (+ v1y v2y) (+ v1z v2z)))

  (v-scalar-* [{:keys [x y z]} s] (Vector. (* x s) (* y s) (* z s)))

  (magnitude [{:keys [x y z]}]
    (let [pow2 #(js/Math.pow % 2)]
      (js/Math.sqrt (+ (pow2 x) (pow2 y) (pow2 z)))))

  (normalize [v] (v-scalar-* v (/ 1 (magnitude v)))))

(defn new-vector
  ([x y] (Vector. x y 0))
  ([x y z] (Vector. x y z)))
