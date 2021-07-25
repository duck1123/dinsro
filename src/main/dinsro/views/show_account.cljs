(ns dinsro.views.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [dinsro.ui.show-account :as u.show-account]
   [taoensso.timbre :as log]))

(defsc ShowAccountPage
  [_this {::keys [account transactions]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::account      {}
                   ::transactions {}}
   :query         [{::account (comp/get-query u.show-account/ShowAccount)}
                   {::transactions (comp/get-query u.account-transactions/AccountTransactions)}]
   :route-segment ["accounts" ::m.accounts/id]
   :will-enter
   (fn [app {::m.accounts/keys [id]}]
     (df/load app [::m.accounts/id id] u.show-account/ShowAccount
              {:target [:page/id ::page ::account]})

     (df/load! app :all-transactions
               u.index-transactions/IndexTransactionLine
               {:target [:component/id ::u.account-transactions/AccountTransactions
                         ::u.account-transactions/transactions
                         ::u.index-transactions/transactions]})

     (dr/route-immediate (comp/get-ident ShowAccountPage {})))}
  (bulma/page
   (bulma/box
    (u.show-account/ui-show-account account))
   (u.account-transactions/ui-account-transactions transactions)))
