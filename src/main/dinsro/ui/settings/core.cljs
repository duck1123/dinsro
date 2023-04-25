(ns dinsro.ui.settings.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]))

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {:component/id ::Dashboard}
   :query         [:component/id]
   :route-segment ["dashboard"]}
  (dom/div {} "Dashboard"))

(defrouter Router
  [_this {:keys [current-state pending-path-segment]
          :as props}]
  {:router-targets [Dashboard]}
  (case current-state
    :pending (dom/div :.ui.segment "Loading... " (pr-str pending-path-segment))
    :failed (dom/div :.ui.segment "Route Failed "  (pr-str pending-path-segment))
    (dom/div {}
      (dom/div :.ui.segment
        (dom/p {} "Core router failed to match any target")
        (dom/code {} (pr-str props))))))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::CorePage])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  ((comp/factory Router) router))
