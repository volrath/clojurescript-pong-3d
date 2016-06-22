(ns pong.frontend.routes
  (:require [clojure.set :refer [rename-keys]]
            [domkm.silk :as silk]
            [pushy.core :as pushy]))

(def routes (silk/routes [[:home [[]]]
                          [:ranking [["ranking"]]]
                          [:game [["game" :uuid]]]]))

(def url-for (partial silk/depart routes))

(def current-route (atom {:name :home}))

(defn- sanitize-silk-keywords [matched-route]
  (rename-keys matched-route {:domkm.silk/name    :name
                              :domkm.silk/pattern :pattern
                              :domkm.silk/routes  :routes
                              :domkm.silk/url     :url}))

(defn- parse-url [url]
  (-> (silk/arrive routes url)
      (sanitize-silk-keywords)))

(defn- dispatch-route [matched-route]
  (reset! current-route matched-route))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))
