(ns dinsro.ui.admin.core.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]))

(def index-page-key :admin-core-dashboard)
(def router-key :dinsro.ui.admin/Router)

(defsc Page
  [_this _props]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key}
   :query         [[::dr/id router-key]
                   ::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (ui-segment {}
    (dom/h1 "Dashboard")
    (dom/p "TODO: Admin Core Dashboard")))

(m.navlinks/defroute :admin-core-dashboard
  {::m.navlinks/label         "Dashboard"
   ::m.navlinks/description   "Admin Core Dashboard"
   ::m.navlinks/control       ::Page
   ::m.navlinks/parent-key    :admin-core
   ::m.navlinks/router        :admin-core
   ::m.navlinks/required-role :admin})
