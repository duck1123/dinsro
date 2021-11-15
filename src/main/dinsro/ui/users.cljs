(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.joins :as m.joins]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def override-form true)

(defattr accounts-link ::m.users/accounts :ref
  {ao/cardinality      :one
   ao/identities       #{::m.users/id}
   ao/target           ::m.accounts/id
   ::report/column-EQL {::m.users/accounts (comp/get-query u.links/AccountLink)}})

(form/defsc-form UserForm
  [this {::m.users/keys [name] :as props}]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    m.joins/user-accounts
                    m.joins/user-categories
                    m.joins/user-ln-nodes
                    m.joins/user-transactions]
   fo/cancel-route ["users"]
   fo/field-styles {::m.users/accounts     :link-subform
                    ::m.users/categories   :link-subform
                    ::m.users/ln-nodes     :link-subform
                    ::m.users/transactions :link-subform}
   fo/route-prefix "user"
   fo/subforms     {::m.users/accounts     {fo/ui u.links/AccountLinkForm}
                    ::m.users/categories   {fo/ui u.links/CategoryLinkForm}
                    ::m.users/ln-nodes     {fo/ui u.links/NodeLinkForm}
                    ::m.users/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/title        "User"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} name))))

(form/defsc-form AdminUserForm
  [_this _props]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin/user"
   fo/title        "User"})

(report/defsc-report UsersReport
  [_this _props]
  {ro/form-links       {::m.users/name UserForm}
   ro/columns          [m.users/name]
   ro/source-attribute ::m.users/all-users
   ro/title            "Users"
   ro/route            "users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})

(report/defsc-report AdminIndexUsersReport
  [_this _props]
  {ro/columns          [m.users/name m.users/role]
   ro/controls         {::new-user {:label  "New User"
                                    :type   :button
                                    :action (fn [this] (form/create! this AdminUserForm))}}
   ro/form-links       {::m.users/name AdminUserForm}
   ro/source-attribute ::m.users/all-users
   ro/title            "Users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})
