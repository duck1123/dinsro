(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-debits :as u.user-debits]
   [dinsro.ui.user-ln-nodes :as u.user-ln-nodes]
   [dinsro.ui.user-pubkeys :as u.user-pubkeys]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [dinsro.ui.user-wallets :as u.user-wallets]))

;; [[../actions/users.clj][User Actions]]
;; [[../joins/users.cljc][User Joins]]
;; [[../model/users.cljc][User Models]]

(def menu-items
  [{:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.user-accounts/SubPage"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.user-debits/SubPage"}
   {:key   "ln-nodes"
    :name  "LN Nodes"
    :route "dinsro.ui.user-ln-nodes/SubPage"}
   {:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.user-pubkeys/SubPage"}
   {:key   "transactions"
    :name  "Transactions"
    :route "dinsro.ui.user-transactions/SubPage"}
   {:key   "wallets"
    :name  "Wallets"
    :route "dinsro.ui.user-wallets/SubPage"}])

(defrouter Router
  [_this _props]
  {:router-targets
   [u.user-accounts/SubPage
    u.user-debits/SubPage
    u.user-ln-nodes/SubPage
    u.user-pubkeys/SubPage
    u.user-transactions/SubPage
    u.user-wallets/SubPage]})

(def ui-router (comp/factory Router))

(defsc ShowUser
  [_this {::m.users/keys [id name]
          :ui/keys       [router]}]
  {:route-segment ["users" :id]
   :query         [::m.users/name
                   ::m.users/id
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.users/name   ""
                   ::m.users/id     nil
                   :ui/router {}}

   :ident         ::m.users/id
   :pre-merge     (u.links/page-merger
                   ::m.users/id
                   {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.users/id ::ShowUser)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show User " (str name)))
   (u.links/ui-nav-menu {:id id :menu-items menu-items})
   (ui-router router)))

(form/defsc-form AdminUserForm
  [_this _props]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin-user"
   fo/title        "Admin User"})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.users/name]
   ro/source-attribute ::m.users/index
   ro/title            "Users"
   ro/route            "users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.users/name m.users/role]
   ro/controls         {::new-user {:label  "New User"
                                    :type   :button
                                    :action (fn [this] (form/create! this AdminUserForm))}
                        ::refresh  u.links/refresh-control}
   ro/form-links       {::m.users/name AdminUserForm}
   ro/source-attribute ::m.users/index
   ro/title            "Admin Users"
   ro/row-pk           m.users/id
   ro/route            "users"
   ro/run-on-mount?    true})
