(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]))

(defsc AdminAccounts
  [_this {::keys [report]}]
  {:query             [{::report (comp/get-query u.accounts/AdminIndexAccountsReport)}]
   :initial-state     {::report {}}
   :ident             (fn [] [:component/id ::AdminAccounts])
   :route-segment     ["accounts"]
   :componentDidMount (fn [this] (report/run-report! this))}
  (dom/div {}
    (dom/p "Admin Accounts")
    (u.accounts/ui-admin-index-accounts report)))

(defsc AdminUsers
  [_this {::keys [report]}]
  {:query             [{::report (comp/get-query u.users/AdminIndexUsersReport)}]
   :initial-state     {::report {}}
   :ident             (fn [] [:component/id ::AdminUsers])
   :route-segment     ["users"]
   :componentDidMount (fn [this] (report/run-report! this))}
  (dom/div {}
    (dom/p "Admin Users")
    (u.users/ui-admin-index-users report)))

(defsc AdminCategories
  [_this _props]
  {:query         []
   :initial-state {}
   :ident         (fn [] [:component/id ::AdminCategories])
   :route-segment ["categories"]}
  (dom/div {}
    (dom/p "Admin Categories")))

(defrouter AdminRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.users/AdminIndexUsersReport
                    u.categories/AdminIndexCategoriesReport
                    u.ln.nodes/AdminLNNodesReport
                    u.accounts/AdminIndexAccountsReport]}
  (dom/div :.admin-router
    (dom/h2 {} "Admin Router")
    (case current-state
      :pending (dom/div {} "Loading...")
      :failed  (dom/div {} "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {}
        (dom/div "No route selected.")))))

(def ui-admin-router (comp/factory AdminRouter))

(defsc AdminPage
  [this {:keys [admin-router]}]
  {:query         [{:admin-router (comp/get-query AdminRouter)}]
   :initial-state {:admin-router {}}
   :ident         (fn [] [:component/id ::AdminPage])
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (dom/h1 "Admin Page")
    (ui-menu
     {:items [{:key   "users"
               :name  "users"
               :route u.users/AdminIndexUsersReport}
              {:key   "categories"
               :name  "Categories"
               :route u.categories/AdminIndexCategoriesReport}
              {:key   "ln-nodes"
               :name  "LN Nodes"
               :route u.ln.nodes/AdminLNNodesReport}
              {:key   "accounts"
               :name  "Accounts"
               :route u.accounts/AdminIndexAccountsReport}]
      :onItemClick
      (fn [_e d]
        (let [route (get (js->clj d) "route")]
          (log/info "route" route)
          (rroute/route-to! this route {})))})
    (ui-admin-router admin-router)))
