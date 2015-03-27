(ns timetrack.tasks
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [timetrack.utils :refer [root-template add-durations format-duration guid]]
            [cljs.core.async :refer [put! <! chan]]
            [clojure.string :as string])
  )


(defcomponent project-view-page [project owner]
  (render-state
   
   [_ state]
   (root-template (dom/h2 "A project page"))
   ))
