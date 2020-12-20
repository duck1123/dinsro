(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountsPage
  [_this {::keys [accounts]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::accounts {}}
   :query [:page/id
           {::accounts (comp/get-query u.user-accounts/UserAccounts)}]
   :route-segment ["accounts"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-accounts u.user-accounts/IndexAccountLine
                 {:target [:page/id
                           ::page
                           ::accounts
                           :dinsro.ui.user-accounts/index-data
                           :dinsro.ui.user-accounts/accounts]})
     (dr/route-immediate (comp/get-ident IndexAccountsPage {})))}
  (bulma/section
   (bulma/container
    (bulma/content
     (u.user-accounts/ui-user-accounts accounts)))))

(def ui-page (comp/factory IndexAccountsPage))
