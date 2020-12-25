(ns dinsro.ui.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defsc ShowAccount
  [_this {::m.accounts/keys [name user-id currency-id account-id]
          :keys [user-link-data]}]
  {:query [::m.accounts/id
           ::m.accounts/name
           ::m.accounts/user-id
           ::m.accounts/currency-id
           ::m.accounts/account-id
           :user-link-data]
   :ident ::m.accounts/id
   :initial-state {::m.accounts/id 1
                   ::m.accounts/name "initial-name"
                   ::m.accounts/currency-id 0
                   ::m.accounts/user-id 0
                   ::m.accounts/account-id 0
                   :user-link-data {}}}
  (dom/div
   (dom/h3 name)
   (dom/p account-id)
   (dom/p
    (tr [:user-label])
    (str user-id)
    (u.links/ui-user-link user-link-data))
   (dom/p
    (tr [:currency-label])
    (str currency-id)
    (comment (u.links/ui-currency-link currency-id)))
   (dom/button
    :.button.is-danger
    "Delete Account")))

(def ui-show-account (comp/factory ShowAccount))
