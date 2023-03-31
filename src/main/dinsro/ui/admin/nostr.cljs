(ns dinsro.ui.admin.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.admin.nostr.badge-acceptances :as u.a.n.badge-acceptances]
   [dinsro.ui.admin.nostr.badge-awards :as u.a.n.badge-awards]
   [dinsro.ui.admin.nostr.badge-definitions :as u.a.n.badge-definitions]
   [dinsro.ui.admin.nostr.connections :as u.a.n.connections]
   [dinsro.ui.admin.nostr.events :as u.a.n.events]
   [dinsro.ui.admin.nostr.filter-items :as u.a.n.filter-items]
   [dinsro.ui.admin.nostr.filters :as u.a.n.filters]
   [dinsro.ui.admin.nostr.pubkeys :as u.a.n.pubkeys]
   [dinsro.ui.admin.nostr.relays :as u.a.n.relays]
   [dinsro.ui.admin.nostr.requests :as u.a.n.requests]
   [dinsro.ui.admin.nostr.runs :as u.a.n.runs]
   [dinsro.ui.links :as u.links]))

(def router-key :dinsro.ui.admin/Router)

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {}
   :query         [[::dr/id router-key]]
   :route-segment ["dashboard"]}
  (dom/div {}
    (dom/h1 "Dashboard")))

(defrouter Router
  [_this _props]
  {:router-targets
   [Dashboard
    u.a.n.badge-acceptances/Report
    u.a.n.badge-awards/Report
    u.a.n.badge-definitions/Report
    u.a.n.connections/Report
    u.a.n.events/Report
    u.a.n.filter-items/Report
    u.a.n.filters/Report
    u.a.n.pubkeys/Report
    u.a.n.relays/Report
    u.a.n.requests/Report
    u.a.n.runs/Report]})

(def menu-items
  [{:key   "dashboard"
    :name  "dashboard"
    :route "dinsro.ui.admin.nostr/Dashboard"}
   {:key   "relays"
    :name  "Relays"
    :route "dinsro.ui.admin.nostr.relays/Report"}
   {:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.admin.nostr.pubkeys/Report"}
   {:key   "events"
    :name  "Events"
    :route "dinsro.ui.admin.nostr.events/Report"}
   {:key   "filters"
    :name  "Filters"
    :route "dinsro.ui.admin.nostr.filters/Report"}
   {:key   "badge-acceptances"
    :name  "Acceptances"
    :route "dinsro.ui.admin.nostr.badge-acceptances/Report"}
   {:key   "badge-awards"
    :name  "Awards"
    :route "dinsro.ui.admin.nostr.badge-awards/Report"}
   {:key   "badge-definitions"
    :name  "Definitions"
    :route "dinsro.ui.admin.nostr.badge-definitions/Report"}
   {:key   "requests"
    :name  "Requests"
    :route "dinsro.ui.admin.nostr.requests/Report"}
   {:key   "connections"
    :name  "Connections"
    :route "dinsro.ui.admin.nostr.connections/Report"}
   {:key   "filter-items"
    :name  "Items"
    :route "dinsro.ui.admin.nostr.filter-items/Report"}
   {:key   "runs"
    :name  "Runs"
    :route "dinsro.ui.admin.nostr.runs/Report"}])

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["nostr"]}
  (comp/fragment
   (u.links/ui-nav-menu {:menu-items menu-items :id nil})
   ((comp/factory Router) router)))
