(ns dinsro.ui.core.networks.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(defn delete-action
  [report-instance {::m.c.blocks/keys [id]}]
  (form/delete! report-instance ::m.c.blocks/id id))

(defn fetch-action
  [report-instance {::m.c.blocks/keys [id]}]
  (comp/transact! report-instance [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))

(def delete-action-button
  {:action delete-action
   :label  "Delete"
   :style  :delete-button})

(def fetch-action-button
  {:action fetch-action
   :label  "Fetch"
   :style  :fetch-button})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/height
                        m.c.blocks/hash
                        m.c.blocks/fetched?]
   ro/control-layout   {:inputs         [[::m.c.networks/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/field-formatters {::m.c.blocks/height #(u.links/ui-block-height-link %3)}
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.blocks/index
   ro/title            "Blocks"})

(def ui-report (comp/factory Report))

(defsc ReportBlock
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (fn [this] (report/start-report! this Report {:route-params (comp/props this)}))
   :ident             (fn [] [:component/id ::ReportBlock])
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}
   :pre-merge         (fn [ctx]
                        (let [{:keys [data-tree]} ctx
                              id                  (::m.c.networks/id data-tree)
                              new-context         {:ui/report (assoc (comp/get-initial-state Report) ::m.c.networks/id id)}
                              merged-data-tree    (merge data-tree new-context)]
                          merged-data-tree))
   :query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]}
  (log/info :ReportBlock/starting {:props props})
  (if report
    (ui-report report)
    (dom/p :.ui.segment "report not loaded")))

(def ui-report-block (comp/factory ReportBlock))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id :dinsro.ui.core.networks/Router]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]}
  (let [router-info (get props [::dr/id :dinsro.ui.core.networks/Router])
        network-id  (::m.c.networks/id router-info)]
    (log/info :SubPage/starting {:props props :report report :router-info router-info})
    (if network-id
      (if report
        (ui-report (assoc report ::m.c.networks/id network-id))
        (dom/div :.ui.segment "Report not loaded"))
      (dom/div :.ui.segment "Network Blocks: No network id"))))

(def ui-sub-page (comp/factory SubPage))
