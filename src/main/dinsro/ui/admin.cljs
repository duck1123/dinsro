(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.admin.accounts :as u.a.accounts]
   [dinsro.ui.admin.categories :as u.a.categories]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.core.peers :as u.a.c.peers]
   [dinsro.ui.admin.currencies :as u.a.currencies]
   [dinsro.ui.admin.debits :as u.a.debits]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.ln.nodes :as u.a.ln.nodes]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.rate-sources :as u.a.rate-sources]
   [dinsro.ui.admin.transactions :as u.a.transactions]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.links :as u.links]))

(defrouter Router
  [_this {:keys [current-state]}]
  {:router-targets [u.a.accounts/AdminReport
                    u.a.c.blocks/AdminReport
                    u.a.c.peers/Report
                    u.a.categories/AdminReport
                    u.a.core/Page
                    u.a.currencies/Report
                    u.a.debits/AdminReport
                    u.a.ln/Page
                    u.a.ln.nodes/AdminReport
                    u.a.nostr/Page
                    u.a.rate-sources/AdminReport
                    u.a.transactions/AdminReport
                    u.a.users/AdminReport
                    u.a.users/AdminUserForm]}
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
   {:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.admin.accounts/AdminReport"}
   {:key   "transactions"
    :name  "Transactions"
    :route "dinsro.ui.admin.transactions/AdminReport"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.admin.debits/AdminReport"}
   {:key   "nostr"
    :name  "Nostr"
    :route "dinsro.ui.admin.nostr/Page"}
   {:key   "core"
    :name  "Core"
    :route "dinsro.ui.admin.core/Page"}
   {:key   "ln"
    :name  "LN"
    :route "dinsro.ui.admin.ln/Page"}
   {:key   "rate-sources"
    :name  "Rate Sources"
    :route "dinsro.ui.admin.rate-sources/AdminReport"}])

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
