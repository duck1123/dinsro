(ns dinsro.views.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.show-account :as u.show-account]
   [taoensso.timbre :as log]))

(defsc ShowAccountPage
  [_this {::keys [account]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::account      {}
                   ::transactions {}}
   :query         [{::account (comp/get-query u.show-account/ShowAccountFull)}
                   {::transactions (comp/get-query u.account-transactions/AccountTransactions)}]
   :route-segment ["accounts" ::m.accounts/id]
   :will-enter
   (fn [app {::m.accounts/keys [id]}]
     (df/load app [::m.accounts/id (new-uuid id)] u.show-account/ShowAccountFull
              {:target [:page/id ::page ::account]})
     (dr/route-immediate (comp/get-ident ShowAccountPage {})))}
  (if (::m.accounts/id account)
    (u.show-account/ui-show-account-full account)
    (dom/p "not loaded")))
