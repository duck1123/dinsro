(ns dinsro.ui.settings.ln.nodes.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.channels :as j.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.channels :as mu.ln.channels]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [lambdaisland.glogi :as log]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.channels/block #(u.links/ui-block-link %2)
                         ::m.ln.channels/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.ln.channels/node]
   ro/control-layout    {:action-buttons [::new ::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh       u.links/refresh-control
                         ::new
                         {:type   :button
                          :label  "New"
                          :action (fn [this]
                                    (let [props                 (comp/props this)
                                          {:ui/keys [controls]} props
                                          id-control            (some
                                                                 (fn [c]
                                                                   (let [{::control/keys [id]} c]
                                                                     (when (= id ::m.ln.nodes/id)
                                                                       c)))

                                                                 controls)
                                          node-id (::control/value id-control)]
                                      (log/info :channels/creating {:props      props
                                                                    :controls   controls
                                                                    :id-control id-control
                                                                    :node-id    node-id})
                                      (form/create! this u.ln.channels/NewForm
                                                    {:initial-state {::m.ln.channels/address "foo"}})))}}
   ro/route            "node-channels"
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.ln.channels/id mu.ln.channels/delete!)]
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.channels/index
   ro/title            "Node Channels"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["channels"]}
  ((comp/factory Report) report))