(ns dinsro.ui.user-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-transaction :as u.f.add-user-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [id initial-value]}]
  {:ident ::m.transactions/id
   :query [::m.transactions/id ::m.transactions/initial-value]}
  (dom/tr
   (dom/td id)
   (dom/td initial-value)))

(def ui-index-transaction-line (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {:keys [transactions]}]
  {:query [{:transactions (comp/get-query IndexTransactionLine)}
           ::m.transactions/id]
   :initial-state {:transactions []}}
  (if (seq transactions)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th "Id")
       (dom/th "initial value")))
     (dom/tbody
      (map ui-index-transaction-line transactions)))
    (dom/div (tr [:no-transactions]))))

(def ui-index-transactions (comp/factory IndexTransactions))

(defsc UserTransactions
  [_this {:keys [form-data button-data index-data]}]
  {:query [{:form-data (comp/get-query u.f.add-user-transaction/AddUserTransactionForm)}
           {:button-data (comp/get-query u.buttons/ShowFormButton)}
           {:index-data (comp/get-query u.index-transactions/IndexTransactions)}]
   :initial-state {:form-data {}
                   :button-data {}
                   :index-data {}}}
  (dom/div
   :.box
   (dom/h2 (tr [:transactions]) (u.buttons/ui-show-form-button button-data))
   (u.f.add-user-transaction/ui-form form-data)
   (ui-index-transactions index-data)))

(def ui-user-transactions
  (comp/factory UserTransactions))
