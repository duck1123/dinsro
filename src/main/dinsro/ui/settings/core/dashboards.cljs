(ns dinsro.ui.settings.core.dashboards
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../ui/settings/core.cljs]]

(def index-page-key :settings-core-dashboard)

(defsc Page
  [_this props]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key}
   :query         [::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (ui-segment {}
    (dom/h1 {}
      "Core Settings Dashboard")
    (dom/p {} "TODO: Put stuff here")))
