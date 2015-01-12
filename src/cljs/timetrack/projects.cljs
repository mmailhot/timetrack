(ns timetrack.projects
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [timetrack.utils :refer [root-template add-durations format-duration]]))

(defn total-time [project]
  (reduce add-durations  (->> project
                              :tasks
                              (map :track-points)
                              flatten
                              (map :duration)
                              )))

(defcomponent project-summary [project owner]
  (render-state
   [_ state]
   (dom/li {:class "project-sum-container"}
           (dom/div {:className "project-sum-header"}
                    (dom/h3 (:name project)))
           (dom/div {:className "project-sum-details"}
                    (dom/p (str "Total Tasks: " (count (:tasks project)))
                           (dom/br "")
                           (str "Total Time: " (format-duration (total-time project))))))))

(defcomponent project-page [app owner]
  (render-state
   [_ state]
   (root-template (dom/section {:className "project-list-container primary-container"}
                               (dom/h2 {:className "primary-header"} "Your Projects")
                               (om/build-all project-summary (:projects app)
                                             {:key :id})))))
