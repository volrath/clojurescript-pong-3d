(ns pong.frontend.components.core
  (:require [pong.frontend.components.game :refer [game-container]]
            [pong.frontend.components.home :refer [home-container]]
            [pong.frontend.routes :refer [current-route]]
            [rum.core :as rum]))

(defmulti dispatcher (fn [route] (:name route)))
(defmethod dispatcher :default [] (game-container))
(defmethod dispatcher :home [] (home-container))

(rum/defc component-dispatcher < rum/reactive []
  (dispatcher (rum/react current-route)))
