(ns dinsro.ui.admin.core.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/peers.cljc]]
;; [[../../../model/core/peers.cljc]]

(def index-page-key :admin-core-peers)
(def model-key ::m.c.peers/id)
(def show-page-key :admin-core-peers-show)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.c.peers/keys [addr id]
                    node             ::m.c.peers/node} (comp/props this)
                   {node-id ::m.c.nodes/id}            node
                   props                               {::m.c.peers/id   id
                                                        ::m.c.peers/addr addr
                                                        ::m.c.peers/node node-id}]
               (comp/transact! this [(mu.c.peers/create! props)])))})

(form/defsc-form NewForm
  [this props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.c.peers/addr
                      m.c.peers/node]
   fo/controls       {::submit submit-button}
   fo/field-options  {::m.c.peers/node u.pickers/node-picker}
   fo/field-styles   {::m.c.peers/node :pick-one}
   fo/id             m.c.peers/id
   fo/route-prefix   "new-peer"
   fo/title          "New Core Peer"}
  (form/render-layout this props))

(def new-peer-control
  {:type   :button
   :label  "New Peer"
   :action (fn [this]
             (let [props                 (comp/props this)
                   {:ui/keys [controls]} props
                   id-control            (some
                                          (fn [c]
                                            (let [{::control/keys [id]} c]
                                              (when (= id ::m.c.nodes/id)
                                                c)))
                                          controls)
                   node-id               (::control/value id-control)]
               (log/info :peers/creating {:props      props
                                          :controls   controls
                                          :id-control id-control
                                          :node-id    node-id})
               (form/create! this NewForm
                             {:initial-state {::m.c.peers/addr "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.peers/block #(u.links/ui-block-link %2)
                         ::m.c.peers/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.c.peers/addr
                         m.c.peers/address-bind
                         m.c.peers/subver
                         m.c.peers/peer-id
                         m.c.peers/node]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::m.c.nodes/id {:type :uuid :label "Nodes"}
                         ::new          new-peer-control
                         ::refresh      u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "peers"
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.c.peers/delete!)]
   ro/row-pk            m.c.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.peers/index
   ro/title             "Core Peers"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.c.peers/id
   :initial-state {::m.c.peers/id nil}
   :query         [::m.c.peers/id]
   :route-segment ["peer" :id]}
  (dom/div {}))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["peers"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["peer" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (ui-show target))
