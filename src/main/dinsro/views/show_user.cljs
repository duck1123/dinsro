(ns dinsro.views.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.users :as m.users]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.show-user :as u.show-user]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-categories :as u.user-categories]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as log]))

(defsc UserAccounts
  [_this _props]
  {:ident ::m.users/id
   :initial-state {}
   :query [{::m.users/accounts (comp/get-query u.user-accounts/IndexAccountLine)}
           ::m.users/id]})

(defsc ShowUserPage
  [_this {::keys [user user-accounts user-categories user-transactions]}]
  {:componentDidUpdate
   (fn [this]
     (let [{::m.users/keys [id]} (::user (comp/props this))]
       (when (seq id)
         (df/load! this (m.users/ident id) UserAccounts)
         (df/load! this {(m.users/ident id) ::m.users/categories}
                   u.user-categories/UserCategories))))
   :ident (fn [] [:page/id ::page])
   :initial-state {::user              {}
                   ::user-accounts     {}
                   ::user-categories   {}
                   ::user-transactions {}}
   :query [::m.users/id
           {::user              (comp/get-query u.show-user/ShowUser)}
           {::user-accounts     (comp/get-query u.user-accounts/UserAccounts)}
           {::user-categories   (comp/get-query u.user-categories/UserCategories)}
           {::user-transactions (comp/get-query u.user-transactions/UserTransactions)}]
   :route-segment ["users" ::m.users/id]
   :will-enter
   (fn [app {::m.users/keys [id]}]
     (df/load app [::m.users/id id] u.show-user/ShowUser
              {:target [:page/id ::page ::user]})
     (dr/route-immediate (comp/get-ident ShowUserPage {})))}
  (bulma/page
   (bulma/box
    (u.show-user/ui-show-user user))
   (u.user-accounts/ui-user-accounts user-accounts)
   (u.user-categories/ui-user-categories user-categories)
   (u.user-transactions/ui-user-transactions user-transactions)))
