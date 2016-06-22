(ns pong.frontend.components.home
  (:require [pong.frontend.routes :refer [url-for]]
            [rum.core :as rum]))

(rum/defc home-container []
  [:div#home
   [:h1 "Hello, welcome to Pong3D"]
   [:a {:href (url-for :game {:uuid "my-uuid"})} "Create New Game"]])
