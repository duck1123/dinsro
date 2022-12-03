(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-ln-nodes :as u.user-ln-nodes]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [dinsro.ui.user-wallets :as u.user-wallets]))

(def override-form true)

(defsc ShowUser
  [_this {::m.users/keys [name]
          :ui/keys       [accounts nodes transactions wallets]}]
  {:route-segment ["users" :id]
   :query         [::m.users/name
                   ::m.users/id
                   {:ui/accounts (comp/get-query u.user-accounts/SubPage)}
                   {:ui/nodes (comp/get-query u.user-ln-nodes/SubPage)}
                   {:ui/transactions (comp/get-query u.user-transactions/SubPage)}
                   {:ui/wallets (comp/get-query u.user-wallets/SubPage)}]
   :initial-state {::m.users/name   ""
                   ::m.users/id     nil
                   :ui/accounts     {}
                   :ui/nodes        {}
                   :ui/transactions {}
                   :ui/wallets      {}}
   :ident         ::m.users/id
   :pre-merge     (u.links/page-merger
                   ::m.users/id
                   {:ui/accounts     u.user-accounts/SubPage
                    :ui/nodes        u.user-ln-nodes/SubPage
                    :ui/transactions u.user-transactions/SubPage
                    :ui/wallets      u.user-wallets/SubPage})
   :will-enter    (partial u.links/page-loader ::m.users/id ::ShowUser)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show User " (str name)))
   (dom/div  :.ui.segment
     (if nodes
       (u.user-ln-nodes/ui-sub-page nodes)
       (dom/p {} "User accounts not loaded")))
   (dom/div  :.ui.segment
     (if accounts
       (u.user-accounts/ui-sub-page accounts)
       (dom/p {} "User accounts not loaded")))
   (dom/div :.ui.segment
     (if transactions
       (u.user-transactions/ui-sub-page transactions)
       (dom/p {} "User transactions not loaded")))
   (dom/div :.ui.segment
     (if wallets
       (u.user-wallets/ui-sub-page wallets)
       (dom/p {} "User wallets not loaded")))))

(form/defsc-form AdminUserForm
  [_this _props]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin-user"
   fo/title        "Admin User"})

(report/defsc-report UsersReport
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

(def ui-admin-index-users (comp/factory AdminReport))
