(ns pong.utils)

(defn log-position [{:keys [x y z]}]
  (.trace js/console (str "X: " x " Y: " y " Z: " z)))
