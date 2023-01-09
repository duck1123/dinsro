(ns dinsro.ui.ln.node-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-accounts!)})

(report/defsc-report Report
  [this props]
  {ro/columns          [m.ln.accounts/wallet
                        m.ln.accounts/address-type
                        m.ln.accounts/node]
   ro/control-layout   {:action-buttons [::fetch ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         fetch-button
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.accounts/wallet #(u.links/ui-wallet-link %2)
                        ::m.ln.accounts/node   #(u.links/ui-node-link %2)}
   ro/source-attribute ::m.ln.accounts/index
   ro/title            "Node Accounts"
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true}
  (log/finer :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(defsc ReportBlock
  [_this {:ui/keys [report]
          :as      props}]
  {:query             [::m.ln.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :ident             (fn [] [:component/id ::ReportBlock])
   :componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (report/start-report! this Report {:route-params props})))
   :pre-merge         (fn [ctx]
                        (log/info :ReportBlock/pre-merge-starting {:ctx ctx})
                        (let [{:keys [data-tree]} ctx
                              id                  (::m.ln.nodes/id data-tree)
                              new-context         {:ui/report
                                                   (assoc (comp/get-initial-state Report)
                                                          ::m.ln.nodes/id id)}
                              merged-data-tree    (merge data-tree new-context)]
                          (log/info :ReportBlock/pre-merge-finished {:data-tree        data-tree
                                                                     :merged-data-tree merged-data-tree})
                          merged-data-tree))
   :initial-state     {::m.ln.nodes/id nil
                       :ui/report        {}}}
  (log/info :ReportBlock/starting {:props props})
  (if report
    (ui-report report)
    (dom/p :.ui.segment "report not loaded")))

(def ui-report-block (comp/factory ReportBlock))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["accounts"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))

(def ui-sub-page (comp/factory SubPage))
