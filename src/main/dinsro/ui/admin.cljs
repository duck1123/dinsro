(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.admin.accounts :as u.a.accounts]
   [dinsro.ui.admin.categories :as u.a.categories]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.currencies :as u.a.currencies]
   [dinsro.ui.admin.debits :as u.a.debits]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.ln.nodes :as u.a.ln.nodes]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.transactions :as u.a.transactions]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.links :as u.links]))

(defrouter AdminRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.a.users/AdminReport
                    u.a.currencies/AdminIndexCurrenciesReport
                    u.a.c.blocks/AdminReport
                    u.a.categories/AdminReport
                    u.a.ln.nodes/AdminReport
                    u.a.transactions/AdminReport
                    u.a.debits/AdminReport
                    u.a.accounts/AdminReport
                    u.a.core/Page
                    u.a.ln/Page
                    u.a.nostr/Page]}
  (dom/div :.admin-router
    (dom/h2 {} "Admin Router")
    (case current-state
      :pending (dom/div {} "Loading...")
      :failed  (dom/div {} "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {}
        (dom/div "No route selected.")))))

(def ui-admin-router (comp/factory AdminRouter))

(def menu-items
  [{:key   "users"
    :name  "users"
    :route "u.users/AdminReport"}
   {:key   "categories"
    :name  "Categories"
    :route "u.categories/AdminReport"}
   {:key   "ln-nodes"
    :name  "LN Nodes"
    :route "u.ln.nodes/AdminReport"}
   {:key   "accounts"
    :name  "Accounts"
    :route "u.accounts/AdminReport"}
   {:key   "transactions"
    :name  "Transactions"
    :route "u.transactions/AdminReport"}
   {:key   "debits"
    :name  "Debits"
    :route "u.debits/AdminReport"}
   {:key   "blocks"
    :name  "Blocks"
    :route "u.c.blocks/AdminReport"}])

(def menu-items2
  [{:key   "users"
    :name  "users"
    :route u.a.users/AdminReport}
   {:key   "categories"
    :name  "Categories"
    :route u.a.categories/AdminReport}
   {:key   "ln-nodes"
    :name  "LN Nodes"
    :route u.a.ln.nodes/AdminReport}
   {:key   "accounts"
    :name  "Accounts"
    :route u.a.accounts/AdminReport}
   {:key   "transactions"
    :name  "Transactions"
    :route u.a.transactions/AdminReport}
   {:key   "debits"
    :name  "Debits"
    :route u.a.debits/AdminReport}
   {:key   "blocks"
    :name  "Blocks"
    :route u.a.c.blocks/AdminReport}])

(defsc AdminPage
  [_this {:ui/keys [admin-router]}]
  {:ident         (fn [] [:component/id ::AdminPage])
   :initial-state {:ui/admin-router {}}
   :query         [{:ui/admin-router (comp/get-query AdminRouter)}]
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (dom/h1 "Admin Page")
    (u.links/ui-nav-menu {:menu-items menu-items :id nil})
    (ui-admin-router admin-router)))
