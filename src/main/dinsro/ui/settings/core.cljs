(ns dinsro.ui.settings.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.settings.core.dashboards :as u.s.c.dashboards]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-core)

(defrouter Router
  [_this {:keys [current-state pending-path-segment]
          :as props}]
  {:router-targets [u.s.c.dashboards/Page]}
  (case current-state
    :pending (dom/div :.ui.segment "Loading... " (pr-str pending-path-segment))
    :failed (dom/div :.ui.segment "Route Failed "  (pr-str pending-path-segment))
    (dom/div {}
      (dom/div :.ui.segment
        (dom/p {} "Core router failed to match any target")
        (dom/code {} (pr-str props))))))

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
    (dom/div :.ui.segment "Failed to load router")))
