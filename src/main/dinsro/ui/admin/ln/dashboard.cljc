(ns dinsro.ui.admin.ln.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]))

(def index-page-key :admin-ln-dashboard)
(def router-key :dinsro.ui.admin/Router)

(defsc IndexPage
  [_this _props]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key}
   :query         [[::dr/id router-key]
                   ::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (ui-segment {}
    (dom/h1 "Dashboard")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Dashboard"
   ::m.navlinks/parent-key    :admin-ln
   ::m.navlinks/router        :admin-ln
   ::m.navlinks/required-role :admin})
