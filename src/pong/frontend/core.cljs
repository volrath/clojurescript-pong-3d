(ns pong.frontend.core
  (:require [pong.frontend.components.core :refer [component-dispatcher]]
            [pong.frontend.controls :refer [controls-listen]]
            [rum.core :as rum]))

(defn init []
  (controls-listen)
  (rum/mount (component-dispatcher) (.getElementById js/document "main")))

(init)
