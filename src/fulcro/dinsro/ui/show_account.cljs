(ns dinsro.ui.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc ShowAccount
  [_this {::m.accounts/keys [account currency name user]}]
  {:ident ::m.accounts/id
   :initial-state {::m.accounts/account  {}
                   ::m.accounts/currency {}
                   ::m.accounts/name     ""
                   ::m.accounts/user     {}}
   :query [{::m.accounts/account  (comp/get-query u.links/AccountLink)}
           {::m.accounts/currency (comp/get-query u.links/CurrencyLink)}
           ::m.accounts/id
           ::m.accounts/name
           {::m.accounts/user     (comp/get-query u.links/UserLink)}
           :user-link-data]}
  (dom/div
   (dom/h3 name)
   (dom/p (u.links/ui-account-link account))
   (dom/p
    (tr [:user-label])
    (u.links/ui-user-link user))
   (dom/p
    (tr [:currency-label])
    (u.links/ui-currency-link currency))
   (dom/button :.button.is-danger "Delete Account")))

(def ui-show-account (comp/factory ShowAccount))
