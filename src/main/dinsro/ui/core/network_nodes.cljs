(ns dinsro.ui.core.network-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.links :as u.links]))

(defn delete-action
  [report-instance {::m.c.nodes/keys [id]}]
  (comp/transact! report-instance [(mu.c.nodes/delete! {::m.c.nodes/id id})]))

(defn fetch-action
  [report-instance {::m.c.nodes/keys [id]}]
  (comp/transact! report-instance [(mu.c.nodes/fetch! {::m.c.nodes/id id})]))

(def delete-action-button
  {:label  "Delete"
   :action delete-action})

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.nodes/name
                        m.c.nodes/host
                        m.c.nodes/initial-block-download?
                        m.c.nodes/block-count]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.nodes/name #(u.links/ui-core-node-link %3)}
   ro/source-attribute ::m.c.nodes/index
   ro/title            "Core Nodes"
   ro/row-actions       [fetch-action-button delete-action-button]
   ro/row-pk           m.c.nodes/id
   ro/run-on-mount?    true
   ro/route            "nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id :dinsro.ui.core.networks/Router]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {:ui/report {}}
   :route-segment     ["nodes"]
   :ident             (fn [] [:component/id ::SubPage])}
  (let [router-info (get props [::dr/id :dinsro.ui.core.networks/Router])]
    (if (::m.c.networks/id router-info)
      (ui-report report)
      (dom/p {} "Node ID not set"))))

(def ui-sub-page (comp/factory SubPage))
