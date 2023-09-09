(ns dinsro.ui.core.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.core.peers :as u.f.c.peers]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/peers.cljc]]
;; [[../../model/core/peers.cljc]]

(def index-page-id :core-peers)
(def model-key ::m.c.peers/id)
(def parent-router-id :core)
(def required-role :user)
(def show-page-id :core-peers-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.peers/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.peers/block #(u.links/ui-block-link %2)
                         ::m.c.peers/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.c.peers/addr
                         m.c.peers/address-bind
                         m.c.peers/subver
                         m.c.peers/peer-id
                         m.c.peers/node]
   ro/controls          {::m.c.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh      u.links/refresh-control
                         ::new-peer     {:type   :button
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
                                                     (form/create! this u.f.c.peers/NewForm
                                                                   {:initial-state {::m.c.peers/addr "foo"}})))}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.peers/index
   ro/title             "Core Peers"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.c.peers/id
   :initial-state {::m.c.peers/id nil}
   :query         [::m.c.peers/id]}
  (dom/div {}
    "TODO: Show Peer"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["peers"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.navlinks/id show-page-id
                   ::m.navlinks/target      {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["peer" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (ui-show target))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Index peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Peer"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
