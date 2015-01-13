(ns timetrack.projects
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [timetrack.utils :refer [root-template add-durations format-duration guid]]
            [cljs.core.async :refer [put! <! chan]]
            [clojure.string :as string])
  )

(def ENTER_KEY 13)



(defn- total-time [project]
  (reduce add-durations  (->> project
                              :tasks
                              (map :track-points)
                              flatten
                              (map :duration)
                              )))


;; Handlers

(defn handle-new-project-keydown [e app owner]
  (when (== (.-which e) ENTER_KEY)
    (let [new-field (om/get-node owner "newProject")]
      (when-not (string/blank? (.. new-field -value trim))
        (let [new-project {:id (guid)
                           :name (.-value new-field)
                           :tasks []}]
          (om/transact! app :projects
                        #(conj % new-project)))
        (set! (.-value new-field) "")
        ))
    false))

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
                               (dom/input {:ref "newProject"
                                           :class "project-new-field project-edit-field"
                                           :placeholder "New project..."
                                           :onKeyDown #(handle-new-project-keydown % app owner)
                                           })
                               (om/build-all project-summary (:projects app)
                                             {:key :id})))))
