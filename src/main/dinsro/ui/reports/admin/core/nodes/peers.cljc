(ns dinsro.ui.reports.admin.core.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.options.core.nodes :as o.c.nodes]
   [dinsro.options.core.peers :as o.c.peers]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.core.peers :as u.f.c.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def model-key o.c.peers/id)
(def parent-model-key o.c.nodes/id)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.peers/delete!))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.buttons/report-action model-key mu.c.nodes/fetch!)})

(def new-button
  {:type   :button
   :label  "New"
   :action (fn [this]
             (let [props                 (comp/props this)
                   {:ui/keys [controls]} props
                   id-control            (some
                                          (fn [c]
                                            (let [{::control/keys [id]} c]
                                              (when (= id o.c.nodes/id)
                                                c)))

                                          controls)
                   node-id (::control/value id-control)]
               (log/info :peers/creating {:props      props
                                          :controls   controls
                                          :id-control id-control
                                          :node-id    node-id})
               (form/create! this u.f.c.peers/NewForm
                             {:initial-state {o.c.peers/addr "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.peers/block #(u.links/ui-admin-block-link %2)
                         o.c.peers/node  #(u.links/ui-admin-core-node-link %2)}
   ro/columns           [m.c.peers/peer-id
                         m.c.peers/addr
                         m.c.peers/subver
                         m.c.peers/connection-type
                         m.c.peers/node]
   ro/control-layout    {:action-buttons [::new ::fetch ::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {parent-model-key {:type :uuid :label "Nodes"}
                         ::refresh        u.links/refresh-control
                         ::fetch          fetch-button
                         ::new            new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.peers/index
   ro/title             "Node Peers"})

(def ui-report (comp/factory Report))
