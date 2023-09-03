(ns dinsro.ui.admin.core.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.loader :as u.loader]))

(def index-page-id :admin-core-dashboard)
(def parent-router-id :admin-core)
(def required-role :admin)
(def router-key :dinsro.ui.admin/Router)

(defsc Page
  [_this _props]
  {:ident         (fn [] [o.navlinks/id index-page-id])
   :initial-state (fn [_props]
                    {o.navlinks/id index-page-id})
   :query         (fn []
                    [[::dr/id router-key]
                     o.navlinks/id])
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (ui-segment {}
    (dom/h1 "Dashboard")
    (dom/p "TODO: Admin Core Dashboard")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::Page
   o.navlinks/description   "Admin Core Dashboard"
   o.navlinks/label         "Dashboard"
   o.navlinks/parent-key    parent-router-id
   o.navlinks/required-role required-role
   o.navlinks/router        parent-router-id})
