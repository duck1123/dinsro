(ns dinsro.ui.settings.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-dashboard)

(defsc Page
  [_this props]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key}
   :query         [::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/debug :Page/starting {:props props})
  (ui-grid {}
    (ui-grid-row {:centered true}
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (ui-segment {}
            (dom/h1 "Settings"))))
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (ui-segment {}
            (dom/h2 "Core Nodes")))))))

(m.navlinks/defroute   :settings-dashboard
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Dashboard"
   ::m.navlinks/parent-key    :setting
   ::m.navlinks/router        :settings
   ::m.navlinks/required-role :user})
