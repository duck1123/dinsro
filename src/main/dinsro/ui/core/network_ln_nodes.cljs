(ns dinsro.ui.core.network-ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name]
   ro/controls         {::refresh         u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Network"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "Network Ln Nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defn SubPage-pre-merge
  [{:keys [data-tree state-map]}]
  (log/finer :SubPage/pre-merge {:data-tree data-tree})
  (let [initial             (comp/get-initial-state Report)
        report-data         (get-in state-map (comp/get-ident Report {}))
        updated-report-data (merge initial report-data)
        updated-data        (-> data-tree (assoc :nodes updated-report-data))]
    (log/finer :SubPage/merged {:updated-data updated-data :data-tree data-tree})
    updated-data))

(defsc SubPage
  [_this {:ui/keys   [report]
          :as        props
          network-id ::m.c.networks/id}]
  {:query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if network-id
      (ui-report report)
      (dom/p {} "Network ID not set"))))

(def ui-sub-page (comp/factory SubPage))
