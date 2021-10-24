(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.currency-transactions :as u.currency-transactions]
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
  [_this {::m.currencies/keys [id name currency-accounts currency-transactions]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id                    nil
                   ::m.currencies/name                  ""
                   ::m.currencies/currency-accounts     {}
                   ::m.currencies/currency-transactions {}}
   :query         [::m.currencies/id
                   ::m.currencies/name
                   {::m.currencies/currency-accounts (comp/get-query u.currency-accounts/CurrencyAccounts)}
                   {::m.currencies/currency-transactions (comp/get-query u.currency-transactions/CurrencyTransactions)}]}
  (if name
    (bulma/page
     (bulma/box
      (dom/div {:className "show-currency"}
        (dom/p name)
        (dom/p (str id))
        (u.buttons/ui-delete-currency-button {::m.currencies/id id})))
     (when currency-accounts
       (u.currency-accounts/ui-currency-accounts currency-accounts))
     (when currency-transactions
       (u.currency-transactions/ui-currency-transactions currency-transactions)))
    (dom/p "No currency")))

(def ui-show-currency-full (comp/factory ShowCurrencyFull))

(defsc ShowCurrencyPage
  [_this {::keys [currency]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::currency {}}
   :query         [{::currency (comp/get-query ShowCurrencyFull)}]
   :route-segment ["currencies" ::m.currencies/id]
   :will-enter
   (fn [app {::m.currencies/keys [id]}]
     (when id
       (df/load app [::m.currencies/id (new-uuid id)] ShowCurrencyFull
                {:target [:page/id ::page ::currency]}))
     (dr/route-immediate (comp/get-ident ShowCurrencyPage {})))}
  (if (::m.currencies/id currency)
    (ui-show-currency-full currency)
    (dom/p "not loaded")))
