(ns dinsro.ui.forms.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.inputs :as u.inputs]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../mocks/ui/forms/transactions.cljc]]
;; [[../../ui/transactions.cljc]]
;; [[../../../../test/dinsro/ui/forms/transactions_test.cljs]]

(def log-props? true)
(def model-key o.transactions/id)
(def override-form? true)

(def delete-debit-mutation nil)

(form/defsc-form NewDebit
  [_this _props]
  {fo/attributes    [m.debits/value
                     m.debits/account]
   fo/field-options {o.debits/account u.pickers/account-picker}
   fo/field-styles  {o.debits/account :pick-one}
   fo/title         "Debit"
   fo/route-prefix  "new-debit"
   fo/id            m.debits/id})

(form/defsc-form NewTransaction
  [this props]
  {fo/attributes     [m.transactions/description
                      m.transactions/date
                      j.transactions/debits]
   fo/default-values {:ui/override-form? true}
   fo/cancel-route   ["transactions"]
   fo/id             m.transactions/id
   fo/route-prefix   "new-transaction"
   fo/subforms       {::j.transactions/debits {fo/ui NewDebit}}
   fo/title          "Transaction"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (dom/div {}
        (dom/div {} "foo")))
    (when log-props?
      (u.debug/ui-props-logger props))))

(form/defsc-form EditForm [_this _props]
  {fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/id            m.transactions/id
   fo/route-prefix  "edit-transaction-form"
   fo/title         "Transaction"})

(defsc CreateTransactionDebitAccountLine
  [_this {::m.accounts/keys [currency name]
          :ui/keys          [debug-props?]
          :as               props}]
  {:initial-state (fn [_]
                    {o.accounts/name     ""
                     o.accounts/currency {}
                     :ui/debug-props?    false})
   :query         (fn []
                    [o.accounts/name
                     o.accounts/currency
                     :ui/debug-props?])}
  (let [{currency-name o.currencies/name} currency]
    (ui-segment {}
      (dom/div {}
        (str "account: " name))
      (dom/div {}
        (str "currency-name: " currency-name))
      (ui-button {} "select")
      (when debug-props?
        (u.debug/ui-props-logger props)))))

(def ui-create-transaction-debit-account-line (comp/factory CreateTransactionDebitAccountLine))

(defsc CreateTransactionDebitLine
  [this {:keys    [current-account position value]
         :ui/keys [debug-props? form-state]
         :as      props}]
  {:initial-state (fn [_props]
                    {:position        0
                     :value           0
                     :current-account nil
                     :ui/debug-props? false
                     :ui/form-state   {}})
   :query         (fn []
                    [:current-account
                     :position
                     :value
                     :ui/debug-props?
                     :ui/form-state])}
  (log/info :CreateTransactionDebitLine/starting {:props props})
  (let [{:keys [accounts]} form-state]
    (ui-segment {:style {:height "100%" :overflow "auto"}}
      (dom/div {} (str "debit position: " position))
      (dom/div {} (str "current account: " current-account))

      (ui-form-input
       {:value    value
        :onChange (fn [evt _] (fm/set-string! this o.debits/value :event evt))
        :label    "Value"})

      (map ui-create-transaction-debit-account-line accounts)

      (u.buttons/delete-button delete-debit-mutation model-key this)
      (when debug-props?
        (dom/div {:style {:overflow "auto" :height "200px"}}
          (u.debug/ui-props-logger props))))))

(def ui-create-transaction-debit-line (comp/factory CreateTransactionDebitLine))

(defsc CreateTransactionForm
  [this {::m.transactions/keys [date description]
         :ui/keys              [debug-props? form-state]
         :as                   props}]
  {:ident         (fn [] [:component/id ::CreateTransactionForm])
   :initial-state (fn [_]
                    {:component/id              ::CreateTransactionForm
                     o.transactions/date        nil
                     o.transactions/description ""
                     o.transactions/id          nil
                     :ui/form-state             {:debits []}
                     :ui/debug-props?           true})
   :query         (fn []
                    [:component/id
                     o.transactions/date
                     o.transactions/description
                     o.transactions/id
                     :ui/debug-props?
                     :ui/form-state])}
  (log/info :CreateTransactionForm/starting {:props props})
  (let [debits (:debits form-state)]
    (dom/div {:style {:height   "100%"
                      :overflow "auto"}}
      (ui-segment {}
        (ui-form-input
         {:value    description
          :onChange (fn [evt _] (fm/set-string! this o.transactions/description :event evt))
          :label    "Description"})

        (ui-form-input
         {:value    date
          :onChange (fn [evt _] (fm/set-string! this o.transactions/date :event evt))
          :label    "Date"})

        (dom/div {:style {:max-height "500px" :overflow "auto"}}
          (map ui-create-transaction-debit-line debits))

        (u.inputs/ui-primary-button
         {:content "Add Debit"}
         {:onClick (fn [] (comp/transact! this [`(mu.transactions/add-debit-line! {})]))}))

      (when debug-props?
        (dom/div {:style {:height "500px" :overflow "hidden"}}
          (u.debug/ui-props-logger props))))))

(def ui-create-transaction-form (comp/factory CreateTransactionForm))
