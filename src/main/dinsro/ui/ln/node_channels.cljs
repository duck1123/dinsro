(ns dinsro.ui.ln.node-channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.form :as form]

   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.mutations.ln.channels :as mu.ln.channels]
   [dinsro.ui.links :as u.links]

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

(report/defsc-report NodeChannelsReport
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
   ro/form-links       {::m.ln.channels/channels-id u.ln.channels/LNChannelForm}
   ro/row-actions      [delete-action-button]
   ro/source-attribute ::m.ln.channels/index
   ro/title            "Node Channels"
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/route            "node-channels"}
  (log/info :NodeChannelsReport/creating {:props props})
  (report/render-layout this))

(def ui-node-channels-report (comp/factory NodeChannelsReport))

(defsc NodeChannelsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.ln.nodes/id}]
  {:query         [::m.ln.nodes/id
                   {:report (comp/get-query NodeChannelsReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodeChannelsSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeChannelsReport)))
   :initial-state {::m.ln.nodes/id nil
                   :report         {}}
   :ident         (fn [] [:component/id ::NodeChannelsSubPage])}
  (log/info :NodeChannelsSubPage/creating {:props props})
  (let [peer-data (assoc-in report [:ui/parameters ::m.ln.nodes/id] node-id)]
    (dom/div :.ui.segment
      #_(dom/code {} (dom/pre {} (pr-str props)))
      (if node-id
        (ui-node-channels-report peer-data)
        (dom/div {} "Node ID not set")))))

(def ui-node-channels-sub-page (comp/factory NodeChannelsSubPage))
