(ns dinsro.ui.admin.nostr
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]))

(def router-key :dinsro.ui.admin/Router)

(defsc Dashboard
  [_this _props]
  {:query         [[::dr/id router-key]]
   :initial-state {}
   :route-segment ["dashboard"]}
  (dom/div {}
    (dom/h1 "Dashboard")))

(defrouter Router
  [_this _props]
  {:router-targets
   [Dashboard]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:query         [{:ui/router (comp/get-query Router)}]
   :initial-state {:ui/router {}}
   :ident         (fn [] [:component/id ::Page])
   :route-segment ["nostr"]}
  (ui-router router))