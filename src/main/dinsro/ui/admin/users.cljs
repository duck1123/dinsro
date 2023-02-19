(ns dinsro.ui.admin.users
  (:require
   ;; [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; [com.fulcrologic.fulcro.dom :as dom]
   ;; [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.users :as j.users]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   ;; [dinsro.ui.user-accounts :as u.user-accounts]
   ;; [dinsro.ui.user-debits :as u.user-debits]
   ;; [dinsro.ui.user-ln-nodes :as u.user-ln-nodes]
   ;; [dinsro.ui.user-pubkeys :as u.user-pubkeys]
   ;; [dinsro.ui.user-transactions :as u.user-transactions]
   ;; [dinsro.ui.user-wallets :as u.user-wallets]
   ))

(form/defsc-form AdminUserForm
  [_this _props]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin-user"
   fo/title        "Admin User"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.users/name m.users/role]
   ro/controls         {::new-user {:label  "New User"
                                    :type   :button
                                    :action (fn [this] (form/create! this AdminUserForm))}
                        ::refresh  u.links/refresh-control}
   ro/form-links       {::m.users/name AdminUserForm}
   ro/source-attribute ::j.users/index
   ro/title            "Admin Users"
   ro/row-pk           m.users/id
   ro/route            "users"
   ro/run-on-mount?    true})
