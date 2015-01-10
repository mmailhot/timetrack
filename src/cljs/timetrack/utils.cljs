(ns timetrack.utils
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]))

(defn root-template [nodes]
  (dom/div {:className "container"}
           (dom/header {:className "main-header"}
                        (dom/h1 "Welcome to Timetrack"))
           nodes))

;;Duration Code
(defn rectify-seconds [{:keys [ hours minutes seconds]}]
  (if (>= seconds 60)
    {:hours hours :minutes (+ minutes 1) :seconds (- seconds 60)}
    {:hours hours :minutes minutes :seconds seconds}))

(defn rectify-minutes [{:keys [ hours minutes seconds]}]
  (if (>= minutes 60)
    {:hours (+ hours 1) :minutes (- minutes 60) :seconds seconds}
    {:hours hours :minutes minutes :seconds seconds}))

(defn add-durations [t1 t2]
  (-> (merge-with + t1 t2)
      rectify-seconds
      rectify-minutes))
