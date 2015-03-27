(ns timetrack.projects
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [timetrack.utils :refer [root-template add-durations format-duration guid]]
            [cljs.core.async :refer [put! <! chan]]
            [clojure.string :as string])
  )

(def ENTER_KEY 13)
(def ESCAPE_KEY 27)



(defn- total-time [project]
  (reduce add-durations  (->> project
                              :tasks
                              (map :track-points)
                              flatten
                              (map :duration)
                              )))

;; Triggers
(defn- submit [e project owner comm]
  (when-let [edit-text (om/get-state owner :edit-text)]
    (if-not (string/blank? (.trim edit-text))
      (do
        (om/update! project :name edit-text)
        (put! comm [:save @project]))
      (put! comm [:destroy @project])))
  false)
(defn- edit [e project owner comm]
  (let [project @project]
    (put! comm [:edit project])
    (om/set-state! owner :edit-text (:name project)))
  )

(defn- change-title [e project owner]
  (om/set-state! owner :edit-text (.. e -target -value)))

(defn- edit-keydown [e project owner comm]
  (condp == (.-keyCode e)
    ESCAPE_KEY (let [project @project]
                 (om/set-state! owner :edit-text (:name project))
                 (put! comm [:cancel project]))
    ENTER_KEY (submit e project owner comm)
    nil))
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

(defn edit-project [app {:keys [id] :as project}]
  (om/update! app :editing id))

(defn destroy-project [app {:keys [id] :as project}]
  (om/transact! app :projects
                (fn [projects] (into [] (remove #(= (:id %) id) projects)))
                [:delete id]))

(defn cancel-edit [app {:keys [id]}]
  (om/update! app :editing nil))

(defn save-project [app {:keys [id]}]
  (om/update! app :editing nil))

(defn handle-event [type app value]
  (case type
    :destroy (destroy-project app value)
    :edit (edit-project app value)
    :save (save-project app value)
    :cancel (cancel-edit app value)
    nil))

(defcomponent project-summary [project owner]
  (init-state [_]
              {:edit-text (:name project)})
  (render-state
   [_ {:keys [comm] :as state}]
   (let [class (cond-> "project-sum-container "
                 (:editing project) (str "editing "))]
     (dom/li {:className class}
             (dom/div {:className "view"}
                      (dom/div {:className "project-sum-header"}
                               (dom/h3 {:onDoubleClick #(edit % project owner comm)} (:name project))
                               (dom/a {:href (str "#/project/" (:id project))
                                       :class "arrow-button"} "⇨"))
                      (dom/div {:className "project-sum-details"}
                               (dom/p (str "Total Tasks: " (count (:tasks project)) " ")
                                      (str "Total Time: " (format-duration (total-time project)) " ")
                                      (dom/a {:onClick #(put! comm [:destroy @project])
                                              :className "project-sum-delete"} "Delete"))))
             (dom/input {:ref "editField" :className "project-edit-field"
                         :value (om/get-state owner :edit-text)
                         :onChange #(change-title % project owner)
                         :onKeyDown #(edit-keydown % project owner comm)
                         :onBlur #(submit % project owner comm)})))))


(defcomponent project-page [app owner]
  (will-mount
   [_]
   (let [comm (chan)]
     (om/set-state! owner :comm comm)
     (go (while true
           (let [[type value] (<! comm)]
             (handle-event type app value))))))
  (render-state
   [_ {:keys [comm] :as state}]
   (root-template (dom/section {:className "project-list-container primary-container"}
                               (dom/h2 {:className "primary-header"} "Your Projects")
                               (dom/input {:ref "newProject"
                                           :class "project-new-field project-edit-field"
                                           :placeholder "New project..."
                                           :onKeyDown #(handle-new-project-keydown % app owner)
                                           })
                               (om/build-all project-summary (:projects app)
                                             {:key :id
                                              :init-state {:comm comm}
                                              :fn (fn [project]
                                                    (cond-> project
                                                      (= (:id project) (:editing app)) (assoc :editing true)))})))))
