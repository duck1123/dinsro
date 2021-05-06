(ns dinsro.ui.forms.add-user-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AddUserTransactionForm
  [_this {::keys [account date value]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::account {}
                   ::date    {}
                   ::value   ""}
   :query [::account
           ::date
           ::value]}
  (dom/div
   (dom/p "value" (str value))
   (dom/p "account" (str account))
   (dom/p "date" (str date))
   (dom/p "submit")))

(def ui-form (comp/factory AddUserTransactionForm))
