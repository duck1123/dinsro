(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as log]))

(defsc IndexAccountsPage
  [_this {:session/keys [current-user-ref]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :session/current-user-ref
               u.user-accounts/UserAccounts
               {:target [:page/id
                         ::page
                         :session/current-user-ref]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {:session/current-user-ref {}}
   :query         [:page/id
                   {:session/current-user-ref (comp/get-query u.user-accounts/UserAccounts)}]
   :route-segment ["accounts"]}
  (when current-user-ref
    (bulma/page
     {:className "index-accounts-page"}
     (u.user-accounts/ui-user-accounts current-user-ref))))

(def ui-page (comp/factory IndexAccountsPage))
