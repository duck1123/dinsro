(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.admin.accounts :as u.a.accounts]
   [dinsro.ui.admin.categories :as u.a.categories]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.currencies :as u.a.currencies]
   [dinsro.ui.admin.debits :as u.a.debits]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.rate-sources :as u.a.rate-sources]
   [dinsro.ui.admin.rates :as u.a.rates]
   [dinsro.ui.admin.transactions :as u.a.transactions]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.links :as u.links]))

(defrouter Router
  [_this {:keys [current-state]}]
  {:router-targets [u.a.accounts/Report
                    u.a.categories/Report
                    u.a.core/Page
                    u.a.currencies/Report
                    u.a.debits/Report
                    u.a.ln/Page
                    u.a.nostr/Page
                    u.a.rates/Show
                    u.a.rates/Report
                    u.a.rate-sources/Report
                    u.a.transactions/Report
                    u.a.users/Report
                    u.a.users/Show
                    u.a.users/UserForm]}
  (dom/div :.admin-router
    (case current-state
      :pending (dom/div {} "Loading...")
      :failed  (dom/div {} "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {}
        (dom/div "No route selected.")))))

(def menu-items
  [{:key   "users"
    :name  "users"
    :route "dinsro.ui.admin.users/Report"}
   {:key   "categories"
    :name  "Categories"
    :route "dinsro.ui.admin.categories/Report"}
   {:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.admin.accounts/Report"}
   {:key   "currencies"
    :name  "Currencies"
    :route "dinsro.ui.admin.currencies/Report"}
   {:key   "transactions"
    :name  "Transactions"
    :route "dinsro.ui.admin.transactions/Report"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.admin.debits/Report"}
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
    :route "dinsro.ui.admin.rate-sources/Report"}
   {:key   "rates"
    :name  "Rates"
    :route "dinsro.ui.admin.rates/Report"}])

(defsc AdminPage
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::AdminPage])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (u.links/ui-nav-menu {:menu-items menu-items :id nil})
    ((comp/factory Router) router)))
