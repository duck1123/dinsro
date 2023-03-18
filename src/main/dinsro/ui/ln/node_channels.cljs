(ns dinsro.ui.ln.node-channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.channels :as mu.ln.channels]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [lambdaisland.glogi :as log]))

(def delete-button
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this _]
             (let [{::m.ln.channels/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.ln.channels/delete! {::m.ln.channels/id id})])))})

(def delete-action-button
  "Delete button for reports"
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this {::m.ln.channels/keys [id]}]
             (log/info :delete-action/clicked {:id id})
             (comp/transact! this [(mu.ln.channels/delete! {::m.ln.channels/id id})]))})

(report/defsc-report Report
  [this props]
  {ro/columns        [m.ln.channels/node]
   ro/control-layout {:action-buttons [::new ::refresh]
                      :inputs         [[::m.ln.nodes/id]]}
   ro/controls       {::m.ln.nodes/id {:type :uuid :label "Nodes"}
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
                                   (form/create! this u.ln.channels/NewChannelForm
                                                 {:initial-state {::m.ln.channels/address "foo"}})))}}
   ro/field-formatters {::m.ln.channels/block #(u.links/ui-block-link %2)
                        ::m.ln.channels/node  #(u.links/ui-core-node-link %2)}
   ro/row-actions      [delete-action-button]
   ro/source-attribute ::m.ln.channels/index
   ro/title            "Node Channels"
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/route            "node-channels"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["channels"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))
