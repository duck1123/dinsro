(ns dinsro.ui.forms.add-user-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserCategoryForm
  [_this _props]
  {:query []}
  (dom/div
   :.box
   (u.buttons/ui-close-button #_close-button)
   (u.inputs/ui-text-input)
   (u.inputs/ui-primary-button)))

(def ui-form (comp/factory AddUserCategoryForm))
