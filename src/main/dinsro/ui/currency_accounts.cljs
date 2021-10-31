(ns dinsro.ui.currency-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc IndexCurrencyAccountLine
  [_ {::m.accounts/keys [link]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/link {}}
   :query         [::m.accounts/id
                   {::m.accounts/link (comp/get-query u.links/AccountLink)}]}
  (dom/tr {}
    (dom/td
     {}
     (u.links/ui-account-link link))))

(def ui-index-currency-account-line (comp/factory IndexCurrencyAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexCurrencyAccounts
  [_ _]
  {:initial-state {::accounts []}
   :query         [::accounts]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (merge {:bar :baz} current-normalized data-tree))})

(def ui-index-currency-accounts (comp/factory IndexCurrencyAccounts))

(defsc CurrencyAccounts
  [_this {::m.currencies/keys [accounts]}]
  {:ident         ::m.currencies/id
   :initial-state (fn [_]
                    {::m.currencies/id       nil
                     ::m.currencies/accounts []})
   :query         [::m.currencies/id
                   {::m.currencies/accounts (comp/get-query IndexCurrencyAccountLine)}]}
  (bulma/box
   (dom/h1
    (tr [:accounts]))
   (dom/hr)
   (dom/div {}
     (if (empty? accounts)
       (dom/div {} (tr [:no-currencies]))
       (dom/table :.ui.table
         (dom/thead {}
           (dom/tr {}
             (dom/th "Name")))
         (dom/tbody {}
           (map ui-index-currency-account-line accounts)))))))

(def ui-currency-accounts (comp/factory CurrencyAccounts))
