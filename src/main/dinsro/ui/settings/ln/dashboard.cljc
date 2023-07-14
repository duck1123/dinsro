(ns dinsro.ui.settings.ln.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-ln-dashboard)

(defsc Page
  [_this props]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key}
   :query         [::m.navlinks/id]
   :route-segment ["dashboard"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (ui-segment {}
    (dom/h1 {} "LN Dashboard")))

(m.navlinks/defroute   :settings-ln-dashboard
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Dashboard"
   ::m.navlinks/parent-key    :settings-ln
   ::m.navlinks/router        :settings-ln
   ::m.navlinks/required-role :user})
