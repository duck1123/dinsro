(ns dinsro.ui.forms.add-user-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AddUserAccountForm
  [_this _props]
  {:query []}
  (dom/div
   :.box
   "Add User Account"))

(def ui-form (comp/factory AddUserAccountForm))
