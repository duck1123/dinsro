(ns dinsro.ui.core.chain-networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.networks/name]
   ro/control-layout   {:inputs         [[::m.c.chains/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.c.chains/id {:type :uuid :label "Chains"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.c.networks/name #(u.links/ui-network-link %3)}
   ro/source-attribute ::m.c.networks/index
   ro/title            "Chain Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defn SubPage-pre-merge
  [{:keys [data-tree state-map]}]
  (log/finer :SubPage-pre-merge/starting {:data-tree data-tree})
  (let [initial             (comp/get-initial-state Report)
        report-data         (get-in state-map (comp/get-ident Report {}))
        updated-report-data (merge initial report-data)
        updated-data        (-> data-tree
                                (assoc :ui/report updated-report-data))]
    (log/finer :SubPage-pre-merge/finished {:updated-data updated-data :data-tree data-tree})
    updated-data))

(def ident-key ::m.c.chains/id)
(def router-key :dinsro.ui.core.chains/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["remote-nodes"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))

(def ui-sub-page (comp/factory SubPage))
