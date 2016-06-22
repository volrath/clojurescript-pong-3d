(ns pong.frontend.core
  (:require [pong.frontend.components.core :refer [component-dispatcher]]
            [pong.frontend.controls :refer [controls-listen]]
            [pong.frontend.routes :refer [app-routes]]
            [rum.core :as rum]))

(defn init []
  (controls-listen)
  (app-routes)
  (rum/mount (component-dispatcher) (.getElementById js/document "main")))

(init)
