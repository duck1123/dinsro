(ns dinsro.ui.forms.add-user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AddUserCategoriesForm
  [_this _props]
  {:query []}
  (dom/div
   :.box
   "Add User Categories"))

(def ui-form (comp/factory AddUserCategoriesForm))
