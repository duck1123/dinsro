(ns dinsro.ui.admin.nostr.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]))

(def router-key :dinsro.ui.admin/Router)

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {}
   :query         [[::dr/id router-key]]
   :route-segment ["dashboard"]}
  (dom/div :.ui.segment
    (dom/h1 "Dashboard")
    (dom/div {}
      "Active Connections")))
