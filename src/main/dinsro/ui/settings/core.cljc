(ns dinsro.ui.settings.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.settings.core.dashboards :as u.s.c.dashboards]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-core)

(defrouter Router
  [_this {:keys [current-state pending-path-segment]
          :as   props}]
  {:router-targets
   [u.s.c.dashboards/Page]}
  (case current-state
    :pending
    (ui-segment {}
      "Loading... "
      (pr-str pending-path-segment))

    :failed
    (ui-segment {}
      "Route Failed "
      (pr-str pending-path-segment))

    (dom/div {}
      (ui-segment {}
        (dom/p {} "Core router failed to match any target")
        (u.debug/log-props props)))))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router] :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/router      {}}
   :pre-merge     (u.loader/page-merger nil {:ui/router [Router {}]})
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (log/info :Page/starting {:props props})
  (if router
    (ui-router router)
    (u.debug/load-error props "settings core page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Core"
   ::m.navlinks/navigate-key  u.s.c.dashboards/index-page-key
   ::m.navlinks/parent-key    :settings
   ::m.navlinks/router        :settings
   ::m.navlinks/required-role :user})
