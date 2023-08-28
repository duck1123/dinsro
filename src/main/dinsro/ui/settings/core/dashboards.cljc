(ns dinsro.ui.settings.core.dashboards
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../ui/settings/core.cljs]]

(def index-page-id :settings-core-dashboard)
(def parent-router-id :settings-core)
(def required-role :user)

(defsc Page
  [_this props]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id}
   :query         [::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (ui-segment {}
    (dom/h1 {}
      "Core Settings Dashboard")
    (dom/p {} "TODO: Put stuff here")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::Page
   o.navlinks/label         "Dashboard"
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
