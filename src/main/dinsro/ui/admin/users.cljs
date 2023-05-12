(ns dinsro.ui.admin.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.users :as j.users]
   [dinsro.menus :as me]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.users :as mu.users]
   [dinsro.ui.admin.users.accounts :as u.a.u.accounts]
   [dinsro.ui.admin.users.categories :as u.a.u.categories]
   [dinsro.ui.admin.users.debits :as u.a.u.debits]
   [dinsro.ui.admin.users.ln-nodes :as u.a.u.ln-nodes]
   [dinsro.ui.admin.users.pubkeys :as u.a.u.pubkeys]
   [dinsro.ui.admin.users.transactions :as u.a.u.transactions]
   [dinsro.ui.admin.users.wallets :as u.a.u.wallets]
   [dinsro.ui.links :as u.links]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.u.accounts/SubPage
    u.a.u.categories/SubPage
    u.a.u.debits/SubPage
    u.a.u.ln-nodes/SubPage
    u.a.u.pubkeys/SubPage
    u.a.u.transactions/SubPage
    u.a.u.wallets/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.users/keys [id name role]
          :ui/keys       [router]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/name ""
                   ::m.users/role nil
                   ::m.users/id   nil
                   :ui/router     {}}
   :pre-merge     (u.links/page-merger ::m.users/id {:ui/router Router})
   :query         [::m.users/name
                   ::m.users/role
                   ::m.users/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["users" :id]
   :will-enter    (partial u.links/page-loader ::m.users/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show User " (str name))
     (dom/div {} (str role)))
   (u.links/ui-nav-menu {:id id :menu-items me/admin-users-menu-items})
   (ui-router router)))

(form/defsc-form UserForm
  [_this _props]
  {fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/id           m.users/id
   fo/route-prefix "admin-user"
   fo/title        "Admin User"})

(def new-button
  {:label  "New User"
   :type   :button
   :action (fn [this] (form/create! this UserForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.users/name              #(u.links/ui-user-link %3)
                         ::j.users/account-count     #(u.links/ui-user-accounts-count-link %3)
                         ::j.users/category-count    #(u.links/ui-user-categories-count-link %3)
                         ::j.users/ln-node-count     #(u.links/ui-user-ln-nodes-count-link %3)
                         ::j.users/transaction-count #(u.links/ui-user-transactions-count-link %3)
                         ::j.users/wallet-count      #(u.links/ui-user-wallets-count-link %3)}
   ro/columns           [m.users/name
                         m.users/role
                         j.users/account-count
                         j.users/category-count
                         j.users/ln-node-count
                         j.users/transaction-count
                         j.users/wallet-count]
   ro/controls          {::new-user new-button
                         ::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "users"
   ro/row-actions       [(u.links/row-action-button "Delete" ::m.users/id mu.users/delete!)]
   ro/row-pk            m.users/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.users/admin-index
   ro/title             "Users"})
