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

(defrouter Router
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

(def menu-items
  [{:key   "users"
    :name  "users"
    :route "dinsro.ui.admin.users/AdminReport"}
   {:key   "categories"
    :name  "Categories"
    :route "dinsro.ui.admin.categories/AdminReport"}
   {:key   "ln-nodes"
    :name  "LN Nodes"
    :route "dinsro.ui.admin.ln.nodes/AdminReport"}
   {:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.admin.accounts/AdminReport"}
   {:key   "transactions"
    :name  "Transactions"
    :route "dinsro.ui.admin.transactions/AdminReport"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.admin.debits/AdminReport"}
   {:key   "blocks"
    :name  "Blocks"
    :route "dinsro.ui.admin.core.blocks/AdminReport"}])

(defsc AdminPage
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::AdminPage])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (dom/h1 "Admin Page")
    (u.links/ui-nav-menu {:menu-items menu-items :id nil})
    ((comp/factory Router) router)))
