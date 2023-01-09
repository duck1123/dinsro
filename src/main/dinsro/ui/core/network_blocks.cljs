(ns dinsro.ui.core.network-blocks
  (:require
   ;; [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   ;; [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
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
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action
   :style  :fetch-button})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/height
                        m.c.blocks/hash
                        m.c.blocks/fetched?]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:inputs [[::m.c.networks/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/height #(u.links/ui-block-height-link %3)}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Blocks"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc ReportBlock
  [_this {:ui/keys [report]
          :as      props}]
  {:query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]
   :ident             (fn [] [:component/id ::ReportBlock])
   :componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (log/info :ReportBlock/did-mount {:props props})
                          (report/start-report! this Report {:route-params props})))
   :pre-merge         (fn [ctx]
                        (log/info :ReportBlock/pre-merge-starting {:ctx ctx})
                        (let [{:keys [data-tree]} ctx
                              id                  (::m.c.networks/id data-tree)
                              new-context         {:ui/report
                                                   (assoc (comp/get-initial-state Report)
                                                          ::m.c.networks/id id)}
                              merged-data-tree    (merge data-tree new-context)]
                          (log/info :ReportBlock/pre-merge-finished {:data-tree        data-tree
                                                                     :merged-data-tree merged-data-tree})
                          merged-data-tree))
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}}
  (log/info :ReportBlock/starting {:props props})
  (if report
    (ui-report report)
    (dom/p :.ui.segment "report not loaded")))

(def ui-report-block (comp/factory ReportBlock))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:query             [{:ui/report (comp/get-query ReportBlock)}
                       [::dr/id :dinsro.ui.core.networks/Router]]
   :initial-state     {:ui/report {}}
   :route-segment     ["blocks"]
   :ident (fn [] [:component/id ::SubPage])}
  (let [router-info (get props [::dr/id :dinsro.ui.core.networks/Router])
        network-id  (::m.c.networks/id router-info)]
    (log/info :SubPage/starting {:props props :report report :router-info router-info})
    (if network-id
      (if report
        (ui-report (assoc report ::m.c.networks/id network-id))
        (dom/div :.ui.segment "Report not loaded"))
      (dom/div :.ui.segment "Network Blocks: No network id"))))

(def ui-sub-page (comp/factory SubPage))
