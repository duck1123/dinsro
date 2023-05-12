(ns dinsro.ui.admin.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.menus :as me]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.core.dashboard :as u.a.c.dashboard]
   [dinsro.ui.admin.core.peers :as u.a.c.peers]
   [dinsro.ui.links :as u.links]))

(def router-key :dinsro.ui.admin/Router)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.c.dashboard/Dashboard
    u.a.c.blocks/Report
    u.a.c.peers/Report]})

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (comp/fragment
   (u.links/ui-nav-menu {:menu-items me/admin-core-menu-items :id nil})
   ((comp/factory Router) router)))
