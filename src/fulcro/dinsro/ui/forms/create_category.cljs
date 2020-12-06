(ns dinsro.ui.forms.create-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateCategoryForm
  [_this {:keys [close-button]}]
  {:query [:close-button]
   :initial-state {:close-button {}}}
  (dom/div
   "Create Category"
   (u.buttons/ui-close-button close-button)
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-category-form (comp/factory CreateCategoryForm))
