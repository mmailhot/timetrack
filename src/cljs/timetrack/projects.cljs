(ns timetrack.projects
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [timetrack.utils :refer [root-template add-durations]]
            ))

(defn total-time [project]
  (reduce add-durations  (->> project
                              :tasks
                              (map :track-points)
                              flatten
                              (map :duration)
                              )))

(defcomponent project-page [app owner]
  (render-state
   [_ state]
   (root-template (dom/h1 (:hours (total-time (first (:projects app))))))))
