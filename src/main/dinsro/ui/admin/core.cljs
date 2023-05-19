(ns dinsro.ui.admin.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.core.dashboard :as u.a.c.dashboard]
   [dinsro.ui.admin.core.peers :as u.a.c.peers]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def router-key :dinsro.ui.admin/Router)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.c.dashboard/Dashboard
    u.a.c.blocks/Report
    u.a.c.peers/Report]})

(defsc Page
  [_this {:ui/keys [nav-menu router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state
   (fn [props]
     (log/trace :Page/initial-state {:props props})
     {:ui/nav-menu (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :admin-core})
      :ui/router   (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (comp/fragment
   (u.menus/ui-nav-menu nav-menu)
   ((comp/factory Router) router)))
