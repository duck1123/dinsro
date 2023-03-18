(ns dinsro.ui.core.network-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.links :as u.links]))

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
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Blocks"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:ident (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id :dinsro.ui.core.networks/Router]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]}

  ((comp/factory Report) report))
