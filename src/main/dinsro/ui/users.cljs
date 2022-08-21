(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.users :as j.users]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [lambdaisland.glogi :as log]))

(def override-form true)

(form/defsc-form UserForm
  [this {::m.users/keys [name] :as props}]
  {fo/id           m.users/id
   fo/attributes   [m.users/name
                    j.users/accounts
                    j.users/categories
                    j.users/ln-nodes
                    j.users/transactions]
   fo/cancel-route ["users"]
   fo/field-styles {::m.users/accounts     :account-table
                    ::m.users/categories   :link-list
                    ::m.users/ln-nodes     :link-list
                    ::m.users/transactions :link-list}
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

(declare ShowUser)

(defn ShowUser-will-enter
  [app {id :id}]
  (let [id    (new-uuid id)
        ident [::m.users/id id]
        state (-> (app/current-state app) (get-in ident))]
    (log/finer :ShowUser-will-enter/starting {:app app :id id :ident ident})
    (dr/route-deferred
     ident
     (fn []
       (log/finer :ShowUser-will-enter/routing
                  {:id       id
                   :state    state
                   :controls (control/component-controls app)})
       (df/load!
        app ident ShowUser
        {:marker               :ui/selected-node
         :target               [:ui/selected-node]
         :post-mutation        `dr/target-ready
         :post-mutation-params {:target ident}})))))

(defn ShowUser-pre-merge
  [{:keys [data-tree state-map current-normalized]}]
  (log/finer :ShowUser-pre-merge/starting {:data-tree          data-tree
                                           :state-map          state-map
                                           :current-noramlized current-normalized})
  (let [user-id (::m.users/id data-tree)]
    (log/finer :ShowUser-pre-merge/parsed {:user-id user-id})
    (let [accounts-data (u.links/merge-state state-map u.user-accounts/SubPage {::m.users/id user-id})
          transactions-data (u.links/merge-state state-map u.user-transactions/SubPage {::m.users/id user-id})]
      (-> data-tree
          (assoc :ui/accounts accounts-data)
          (assoc :ui/transactions transactions-data)))))

(defsc ShowUser
  [_this {::m.users/keys [name]
          :ui/keys       [accounts transactions]}]
  {:route-segment ["users" :id]
   :query         [::m.users/name
                   ::m.users/id
                   {:ui/accounts (comp/get-query u.user-accounts/SubPage)}
                   {:ui/transactions (comp/get-query u.user-transactions/SubPage)}]
   :initial-state {::m.users/name   ""
                   ::m.users/id     nil
                   :ui/accounts     {}
                   :ui/transactions {}}
   :ident         ::m.users/id
   :will-enter    ShowUser-will-enter
   :pre-merge     ShowUser-pre-merge}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show User " (str name)))
   (dom/div  :.ui.segment
     (if accounts
       (u.user-accounts/ui-sub-page accounts)
       (dom/p {} "User accounts not loaded")))
   (dom/div :.ui.segment
     (if transactions
       (u.user-transactions/ui-sub-page transactions)
       (dom/p {} "User transactions not loaded")))))

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

(report/defsc-report AdminIndexUsersReport
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

(def ui-admin-index-users (comp/factory AdminIndexUsersReport))
