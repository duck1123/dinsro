(ns dinsro.ui.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.mutations]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.settings.categories :as u.s.categories]
   [dinsro.ui.settings.core :as u.s.core]
   [dinsro.ui.settings.ln :as u.s.ln]
   [dinsro.ui.settings.rate-sources :as u.s.rate-sources]))

(def menu-items
  [{:key   "dashboard"
    :name  "Dashboard"
    :route "dinsro.ui.settings/Dashboard"}
   {:key   "core"
    :name  "Core"
    :route "dinsro.ui.settings.core/Dashboard"}
   {:name  "Lightning"
    :key   "ln"
    :route "dinsro.ui.settings.ln.payments/Report"}
   {:name  "Rate Sources"
    :key   "rate-sources"
    :route "dinsro.ui.settings.rate-sources/Report"}
   {:name  "Categories"
    :key   "categories"
    :route "dinsro.ui.settings.categories/Page"}])

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {}
   :query         []
   :route-segment ["dashboard"]}
  (dom/div :.twelve.wide.column
    (dom/div :.ui.grid
      (dom/div :.six.wide.column.centered
        (dom/div :.ui.segment
          (dom/h1 "Settings")))
      (dom/div :.six.wide.column.centered
        (dom/div :.ui.segment
          (dom/h2 "Core Nodes"))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [Dashboard
    u.s.core/Page
    u.s.ln/Page
    u.s.rate-sources/Report
    u.s.rate-sources/Show
    u.s.categories/Report]})

(defsc SettingsPage
  [_this {:ui/keys [router]}]
  {:ident         (fn [_] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["settings"]}
  (dom/div :.ui.grid
    (dom/div :.four.wide.column
      (u.links/ui-vertical-menu {:id nil :menu-items menu-items}))
    ((comp/factory Router) router)))
