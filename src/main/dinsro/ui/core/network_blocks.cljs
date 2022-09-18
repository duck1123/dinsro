(ns dinsro.ui.core.network-blocks
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
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
  [this {::m.c.networks/keys [id]
         :ui/keys            [report]
         :as                 props}]
  {:query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]
   :ident             (fn [] [:component/id ::ReportBlock])
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}}
  (dom/div :.ui.segment
    (dom/p {} "Report Block")
    (dom/p {} "ID: " (pr-str id))
    (if report
      (ui-report report)
      (dom/p {} "report not loaded"))))

(def ui-report-block (comp/factory ReportBlock))

(defsc SubPage
  [_this {:ui/keys   [block-report report-block]
          :as        props
          network-id ::m.c.networks/id}]
  {:query             [::m.c.networks/id
                       {:ui/report-block (comp/get-query ReportBlock)}
                       {:ui/block-report (comp/get-query Report)}]
   :componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (log/info :SubPage/did-mount {:this this :props props})
                          (report/start-report! this Report {:route-params props})))
   :initial-state     {::m.c.networks/id nil
                       :ui/block-report  {}
                       :ui/report-block  {}}
   :route-segment     ["blocks"]
   :pre-merge
   (fn [ctx]
     (log/info :pre-merge/starting {:ctx ctx})
     (let [{:keys [data-tree]} ctx
           id                  (::m.c.networks/id data-tree)
           new-context         {:ui/report-block
                                (assoc (comp/get-initial-state ReportBlock)
                                       ::m.c.networks/id id)
                                :ui/block-report
                                (assoc (comp/get-initial-state Report)
                                       ::m.c.networks/id id)}

           merged-data-tree (merge data-tree new-context)]
       (log/info :pre-merge/finished {:data-tree        data-tree
                                      :merged-data-tree merged-data-tree})
       merged-data-tree))
   :will-enter (fn [app route-params]
                 (let [id-str (:id route-params)
                       id     (new-uuid id-str)
                       ident  [::m.c.networks/id id]
                       state  (-> (app/current-state app) (get-in ident))]
                   (log/info :SubPage/will-enter-starting {:id           id
                                                           :app          app
                                                           :route-params route-params
                                                           :state        state})
                   (if id-str
                     (do
                       (log/info :SubPage/routing-immediate {:id id})
                       (dr/route-immediate
                        ident))
                     (log/error :SubPage/no-id {}))))

   :ident (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/starting {:props props})
  (if network-id
    (dom/div :.ui.segment
      (dom/p {} "Network id: " (pr-str network-id))
      (ui-report-block (assoc report-block ::m.c.networks/id network-id)))
    (dom/div :.ui.segment
      "No network id")))

(def ui-sub-page (comp/factory SubPage))
