(ns dinsro.ui.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defsc ShowAccount
  [_this {::keys [name user-id currency-id account-id]}]
  {:query [::name ::user-id ::currency-id ::account-id]
   :initial-state (fn [_] {::name "initial-name"
                           ::currency-id 0
                           ::user-id 0
                           ::account-id 0})}
  (dom/div
   (dom/h3 name)
   (dom/p account-id)
   (dom/p
    (tr [:user-label])
    (str user-id)
    (comment (u.links/ui-user-link user-id)))
   (dom/p
    (tr [:currency-label])
    (str currency-id)
    (comment (u.links/ui-currency-link currency-id)))
   (dom/button "Delete Account")))
