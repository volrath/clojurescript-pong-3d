(ns pong.frontend.components.core
  (:require [pong.frontend.components.game :refer [game-container]]))

(defmulti dispatcher identity)
(defmethod dispatcher :game [] (game-container))

(defn component-dispatcher []
  (dispatcher :game))
