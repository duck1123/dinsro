(ns dinsro.ui.admin.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
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

(def menu-items
  [{:key   "dashboard"
    :name  "Dashboard"
    :route "dinsro.ui.admin.core/Dashboard"}
   {:key   "blocks"
    :name  "Blocks"
    :route "dinsro.ui.admin.core.blocks/AdminReport"}
   {:key   "peers"
    :name  "Peers"
    :route "dinsro.ui.admin.core.peers/AdminReport"}])

(defrouter Router
  [_this _props]
  {:router-targets [Dashboard]})

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (comp/fragment
   (u.links/ui-nav-menu {:menu-items menu-items :id nil})

   ((comp/factory Router) router)))
