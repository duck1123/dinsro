(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin.core :as u.a.core]
   [dinsro.ui.admin.ln :as u.a.ln]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.debits :as u.debits]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.users :as u.users]
   [lambdaisland.glogc :as log]))

(defrouter AdminRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.a.users/AdminReport
                    u.c.blocks/AdminReport
                    u.categories/AdminReport
                    u.currencies/AdminIndexCurrenciesReport
                    u.ln.nodes/AdminReport
                    u.rate-sources/AdminIndexRateSourcesReport
                    u.transactions/AdminReport
                    u.debits/AdminReport
                    u.accounts/AdminReport
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

(defsc AdminPage
  [this {:ui/keys [admin-router]}]
  {:ident         (fn [] [:component/id ::AdminPage])
   :initial-state {:ui/admin-router {}}
   :query         [{:ui/admin-router (comp/get-query AdminRouter)}]
   :route-segment ["admin"]}
  (dom/div :.admin-page
    (dom/h1 "Admin Page")
    (ui-menu
     {:items [{:key   "users"
               :name  "users"
               :route u.users/AdminReport}
              {:key   "categories"
               :name  "Categories"
               :route u.categories/AdminReport}
              {:key   "ln-nodes"
               :name  "LN Nodes"
               :route u.ln.nodes/AdminReport}
              {:key   "accounts"
               :name  "Accounts"
               :route u.accounts/AdminReport}
              {:key "transactions"
               :name "Transactions"
               :route u.transactions/AdminReport}
              {:key   "debits"
               :name  "Debits"
               :route u.debits/AdminReport}
              {:key   "blocks"
               :name  "Blocks"
               :route u.c.blocks/AdminReport}]
      :onItemClick
      (fn [_e d]
        (let [route (get (js->clj d) "route")]
          (log/info :onItemClick/starting {:route route})
          (rroute/route-to! this route {})))})
    (ui-admin-router admin-router)))
