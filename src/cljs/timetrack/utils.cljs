(ns timetrack.utils
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]])
  (:import [goog.ui IdGenerator]))

(defn root-template [nodes]
  (dom/div {:className "container"}
           (dom/header {:className "main-header"}
                        (dom/h1 "Timetrack"))
           nodes))

(defn guid []
  (.getNextUniqueId (.getInstance IdGenerator)))

;;Duration Code
(defn- rectify-seconds [{:keys [ hours minutes seconds]}]
  (if (>= seconds 60)
    {:hours hours :minutes (+ minutes 1) :seconds (- seconds 60)}
    {:hours hours :minutes minutes :seconds seconds}))

(defn- rectify-minutes [{:keys [ hours minutes seconds]}]
  (if (>= minutes 60)
    {:hours (+ hours 1) :minutes (- minutes 60) :seconds seconds}
    {:hours hours :minutes minutes :seconds seconds}))

(defn add-durations [t1 t2]
  (-> (merge-with + t1 t2)
      rectify-seconds
      rectify-minutes))

(defn format-duration [d]
  (if (some? (:hours d))
    (if (== 0 (:hours d))
      (str (:minutes d) "m" (:seconds d) "s")
      (str (:hours d) "h" (:minutes d) "m"))
    "0h0m"))

