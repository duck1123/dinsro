(ns dinsro.ui.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-categories :as u.user-categories]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as log]))

(defsc ShowUser
  [_this {::m.users/keys [id name]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id
                   ::m.users/name]}
  (if id
    (dom/div {}
      (dom/h1 (str id))
      (dom/p name)
      (u.buttons/ui-delete-user-button {::m.users/id id}))
    (dom/p "not loaded")))

(def ui-show-user (comp/factory ShowUser))

(defsc ShowUserFull
  [_this {::m.users/keys [id name
                          user-accounts
                          user-categories
                          user-transactions]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id                nil
                   ::m.users/name              ""
                   ::m.users/user-accounts     {}
                   ::m.users/user-categories   {}
                   ::m.users/user-transactions {}}
   :query         [::m.users/id
                   ::m.users/name
                   {::m.users/user-accounts (comp/get-query u.user-accounts/UserAccounts)}
                   {::m.users/user-categories (comp/get-query u.user-categories/UserCategories)}
                   {::m.users/user-transactions (comp/get-query u.user-transactions/UserTransactions)}]}
  (if id
    (bulma/page
     (bulma/box
      (dom/div {}
        (dom/h1 (str id))
        (dom/p "name:" name)
        (u.buttons/ui-delete-user-button {::m.users/id id})))
     (u.user-accounts/ui-user-accounts user-accounts)
     (u.user-categories/ui-user-categories user-categories)
     (u.user-transactions/ui-user-transactions user-transactions))
    (dom/p {} "No id")))

(def ui-show-user-full (comp/factory ShowUserFull))

(defsc UserAccounts
  [_this _props]
  {:ident         ::m.users/id
   :initial-state {::m.users/id       nil
                   ::m.users/accounts []}
   :query         [{::m.users/accounts (comp/get-query u.user-accounts/IndexAccountLine)}
                   ::m.users/id]})

(defsc ShowUserPage
  [_this {::m.users/keys [link]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::m.users/link {}}
   :query         [::m.users/id
                   {::m.users/link (comp/get-query ShowUserFull)}]
   :route-segment ["users" ::m.users/id]
   :will-enter
   (fn [app {::m.users/keys [id]}]
     (when id
       (df/load app [::m.users/id (new-uuid id)] ShowUserFull
                {:target [:page/id ::page ::m.users/link]}))
     (dr/route-immediate (comp/get-ident ShowUserPage {})))}
  (when (::m.users/id link) (ui-show-user-full link)))
