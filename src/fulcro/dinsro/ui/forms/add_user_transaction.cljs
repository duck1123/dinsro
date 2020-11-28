(ns dinsro.ui.forms.add-user-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AddUserTransactionForm
  [_this _props]
  {:query []}
  (dom/div
   :.box
   "Add User Transaction"))

(def ui-form (comp/factory AddUserTransactionForm))
