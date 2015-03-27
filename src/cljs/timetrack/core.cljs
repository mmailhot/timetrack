(ns timetrack.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [secretary.core :as sec :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [timetrack.utils :refer [root-template]]
            [timetrack.projects :refer [project-page]]
            [timetrack.tasks :refer [project-view-page]])
  (:import goog.History))

(sec/set-config! :prefix "#")

(let [history (History.)
      navigation EventType/NAVIGATE]
  (goog.events/listen history
                     navigation
                     #(-> % .-token sec/dispatch!))
  (doto history (.setEnabled true)))

(def app-state (atom {:text "Welcome to Timetrack!"
                          :projects [{:id 1
                                      :name "A project"
                                      :tasks [{:id 6
                                               :name "Task 1"
                                               :track-points [{:id 1324 :duration {:hours 0 :minutes 30 :seconds 0}}
                                                              {:id 1325 :duration {:hours 1 :minutes 20 :seconds 45}}
                                                              {:id 1326 :duration {:hours 0 :minutes 45 :seconds 35}}]
                                               }]}
                                     {:id 2
                                      :name "Another project"
                                      :tasks [{:id 7
                                               :name "Task 1"
                                               :track-points [{:id 1327 :duration {:hours 0 :minutes 50 :seconds 0}}
                                                              {:id 1328 :duration {:hours 1 :minutes 20 :seconds 45}}
                                                              {:id 1329 :duration {:hours 0 :minutes 45 :seconds 35}}]
                                               }]}
                                     {:id 3
                                      :name "A project"
                                      :tasks [{:id 8
                                               :name "Task 1"
                                               :track-points [{:id 1321 :duration {:hours 0 :minutes 6 :seconds 0}}
                                                              {:id 1322 :duration {:hours 0 :minutes 5 :seconds 45}}
                                                              {:id 1323 :duration {:hours 0 :minutes 35 :seconds 35}}]
                                               }]}]}))

(sec/defroute homepage "/" []
  (om/root project-page
           app-state
           {:target (. js/document (getElementById "app"))}))

(sec/defroute project-detail-page "/project/:pid" [pid]
  (om/root project-view-page
           (first (filter #(== (:id %) pid) (:projects app-state)))
           {:target (. js/document (getElementById "app"))}))

(defn main []
  (-> js/document
      .-location
      (set! "#/"))
  (sec/dispatch! "/")
  )
