(ns dinsro.ui.forms.create-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateCategoryForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   "Create Category"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-category-form (comp/factory CreateCategoryForm))
