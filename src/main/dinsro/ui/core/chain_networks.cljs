(ns dinsro.ui.core.chain-networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.networks/id
                        m.c.networks/name
                        m.c.networks/chain]
   ro/controls         {::refresh       u.links/refresh-control
                        ::m.c.chains/id {:type :uuid :label "Chains"}}
   ro/control-layout   {:inputs         [[::m.c.chains/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                        ::m.c.networks/name  #(u.links/ui-network-link %3)}
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

(defsc SubPage
  [_this {:ui/keys [report]
          chain-id ::m.c.chains/id
          :as      props}]
  {:query         [::m.c.chains/id
                   {:ui/report (comp/get-query Report)}]
   :pre-merge     SubPage-pre-merge
   :initial-state {::m.c.chains/id nil
                   :ui/report      {}}
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident         (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/starting {:props props})
  (dom/div :.ui.segment
    (if chain-id
      (ui-report report)
      (dom/p {} "Chain id not set"))))

(def ui-sub-page (comp/factory SubPage))
