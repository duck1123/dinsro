(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.bulma :as bulma]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc ShowCurrency
  [_this {::m.currencies/keys [id name]}]
  {:query         [::m.currencies/id ::m.currencies/name]
   :ident         ::m.currencies/id
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/name ""}}
  (dom/div {}
    (dom/p name)
    (dom/p id)
    (u.buttons/ui-delete-currency-button {::m.currencies/id id})))

(def ui-show-currency (comp/factory ShowCurrency))

(defsc ShowCurrencyFull
  [_this {::m.currencies/keys [id name accounts]}]
  {:ident         ::m.currencies/id
   :initial-state (fn []
                    {::m.currencies/id       nil
                     ::m.currencies/name     ""
                     ::m.currencies/accounts (comp/get-initial-state u.currency-accounts/CurrencyAccounts)})
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [default     {:show-currency-full true}
                          merged-data (merge default current-normalized data-tree)]
                      merged-data))
   :query         [::m.currencies/id
                   ::m.currencies/name
                   {::m.currencies/accounts (comp/get-query u.currency-accounts/CurrencyAccounts)}]}
  (bulma/page
   (bulma/box
    (dom/div
      (dom/p name)
      (dom/p (str id))
      (u.buttons/ui-delete-currency-button {::m.currencies/id id})))
   (u.currency-accounts/ui-currency-accounts accounts)))

(def ui-show-currency-full (comp/factory ShowCurrencyFull))
