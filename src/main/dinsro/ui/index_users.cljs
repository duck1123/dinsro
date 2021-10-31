(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-accounts :as u.user-accounts :refer [UserAccounts]]
   [dinsro.ui.user-categories :as u.user-categories :refer [UserCategories]]
   [dinsro.ui.user-transactions :as u.user-transactions :refer [UserTransactions]]
   [taoensso.timbre :as log]))

(defsc IndexUserLine
  [_this {::m.users/keys [id link]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   ""
                   ::m.users/link {}}
   :query         [::m.users/id
                   {::m.users/link (comp/get-query u.links/ui-user-link)}]}
  (dom/tr {}
    (dom/th (u.links/ui-user-link link))
    (dom/th (u.buttons/ui-delete-user-button {::m.users/id id}))))

(def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/id}))

(def users-path "/admin/users")

(defsc IndexUsers
  [_this {::keys [items]}]
  {:initial-state {::items []}
   :query         [{::items (comp/get-query IndexUserLine)}]}
  (if-not (seq items)
    (dom/div {} (dom/p (tr [:no-users])))
    (dom/div {}
      (dom/p {}
        (dom/a {:href users-path} "Users"))
      (dom/table :.table.ui
        (dom/thead {}
          (dom/tr {}
            (dom/th (tr [:username]))
            (dom/th "Buttons")))
        (dom/tbody {}
          (map ui-index-user-line items))))))

(def ui-index-users (comp/factory IndexUsers))

(defsc IndexUsersFull
  [_this {::keys [items]
          ::m.users/keys [user-accounts user-categories user-transactions]}]
  {:initial-state {::items                     []
                   ::m.users/user-accounts     {}
                   ::m.users/user-categories   {}
                   ::m.users/user-transactions {}}
   :query         [{::items (comp/get-query IndexUserLine)}
                   {::m.users/user-accounts (comp/get-query UserAccounts)}
                   {::m.users/user-categories (comp/get-query UserCategories)}
                   {::m.users/user-transactions (comp/get-query UserTransactions)}]}
  (bulma/page
   (bulma/box
    (if-not (seq items)
      (dom/div {} (dom/p (tr [:no-users])))
      (dom/div {}
        (dom/p {}
          (dom/a {:href users-path} "Users"))
        (dom/table :.table.ui
          (dom/thead {}
            (dom/tr {}
              (dom/th (tr [:username]))
              (dom/th "Buttons")))
          (dom/tbody {}
            (map ui-index-user-line items))))))
   (u.user-accounts/ui-user-accounts user-accounts)
   (u.user-categories/ui-user-categories user-categories)
   (u.user-transactions/ui-user-transactions user-transactions)))

(def ui-index-users-full (comp/factory IndexUsersFull))

(defsc IndexUsersPage
  [_this {::keys [users]}]
  {:componentDidMount
   (fn [this]
     (df/load! this ::m.users/all-users IndexUserLine
               {:target [:page/id
                         ::page
                         ::users
                         :dinsro.ui.index-users/items]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::users {}}
   :query         [{::users (comp/get-query IndexUsers)}]
   :route-segment ["users"]}
  (bulma/page
   (bulma/box
    (dom/h1 (tr [:users-page "Users Page"]))
    (dom/hr)
    (ui-index-users users))))

(def ui-page (comp/factory IndexUsersPage))
