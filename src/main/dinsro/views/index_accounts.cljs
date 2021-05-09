(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as log]))

(defsc IndexAccountsPage
  [_this {::keys [user-accounts]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :all-accounts
               u.user-accounts/IndexAccountLine
               {:target [:component/id
                         ::u.user-accounts/UserAccounts
                         ::u.user-accounts/accounts
                         ::u.user-accounts/accounts]}))
   :ident (fn [] [:page/id ::page])
   :initial-state {::user-accounts {}}
   :query [:page/id
           {::user-accounts (comp/get-query u.user-accounts/UserAccounts)}]
   :route-segment ["accounts"]}
  (bulma/page
   (u.user-accounts/ui-user-accounts user-accounts)))

(def ui-page (comp/factory IndexAccountsPage))
