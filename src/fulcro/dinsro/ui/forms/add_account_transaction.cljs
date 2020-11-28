(ns dinsro.ui.forms.add-account-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AddAccountTransactionForm
  [_this _props]
  {:query []}
  (dom/div "add account transaction form"))

(def ui-add-account-transaction-form
  (comp/factory AddAccountTransactionForm))
