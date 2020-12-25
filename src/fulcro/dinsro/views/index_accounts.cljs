(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountsPage
  [_this {::keys [accounts]}]
  {:query [:page/id
           {::accounts (comp/get-query u.user-accounts/UserAccounts)}]
   :initial-state {::accounts {}}
   :ident (fn [] [:page/id ::page])
   :route-segment ["accounts"]}
  (bulma/section
   (bulma/container
    (bulma/content
     (u.user-accounts/ui-user-accounts accounts)))))

(def ui-page (comp/factory IndexAccountsPage))
