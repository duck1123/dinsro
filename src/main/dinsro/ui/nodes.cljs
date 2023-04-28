(ns dinsro.ui.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [dinsro.ui.nodes.core :as u.no.core]
   [dinsro.ui.nodes.ln :as u.no.ln]))

(defsc Dashboard
  [_this {:ui/keys [core-report ln-report]}]
  {:componentDidMount (fn [this]
                        (report/start-report! this u.no.core/Report {:route-params (comp/props this)})
                        (report/start-report! this u.no.ln/Report {:route-params (comp/props this)}))
   :ident             (fn [] [:component/id ::Dashboard])
   :initial-state     {:ui/core-report {}
                       :ui/ln-report   {}}
   :query             [{:ui/core-report (comp/get-query u.no.core/Report)}
                       {:ui/ln-report (comp/get-query u.no.ln/Report)}]
   :route-segment     ["dashboard"]}
  (dom/div :.ui.segment
    (dom/div :.ui.grid
      (dom/div :.two.column.row
        (dom/div :.ui.column
          (dom/div :.ui.container
            (dom/div :.ui.segment
              (u.no.core/ui-report core-report))))
        (dom/div :.ui.column
          (dom/div :.ui.container
            (dom/div :.ui.segment
              (u.no.ln/ui-report ln-report))))))))

(defrouter Router
  [_this {:keys [route-factory route-props]}]
  {:router-targets
   [Dashboard]}
  (dom/div :.router
    (route-factory route-props)))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["nodes"]}
  (dom/div {}
    ((comp/factory Router) router)))
