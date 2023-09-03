(ns dinsro.ui.admin.ln.remote-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.remote-nodes :as j.ln.remote-nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.remote-nodes :as mu.ln.remote-nodes]
   [dinsro.options.ln.remote-nodes :as o.ln.remote-nodes]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.remote-nodes.peers :as u.ln.rn.peers]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/remote_nodes.cljc]]
;; [[../../../model/ln/remote_nodes.cljc]]

(def index-page-id :admin-ln-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-remote-nodes-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.ln.remote-nodes/delete!))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.ln.rn.peers/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {:ui/keys                 [peers router]
          ::m.ln.remote-nodes/keys [id pubkey]}]
  {:ident         ::m.ln.remote-nodes/id
   :initial-state (fn [props]
                    {o.ln.remote-nodes/id     (model-key props)
                     o.ln.remote-nodes/pubkey ""
                     :ui/peers                (comp/get-initial-state u.ln.rn.peers/SubPage {})
                     :ui/router               (comp/get-initial-state Router {})})
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/peers  [u.ln.rn.peers/SubPage {}]
                     :ui/router [Router {}]})
   :query         (fn []
                    [o.ln.remote-nodes/id
                     o.ln.remote-nodes/pubkey
                     {:ui/peers (comp/get-query u.ln.rn.peers/SubPage)}
                     {:ui/router (comp/get-query Router)}
                     [df/marker-table '_]])}
  (dom/div {}
    (ui-segment {}
      (dom/h1 {} "Remote Node")
      (dom/p {} "id: " (pr-str id))
      (dom/p {} "pubkey: " (str pubkey)))
    (u.ln.rn.peers/ui-sub-page peers)
    (ui-router router)))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.remote-nodes/pubkey
                        m.ln.remote-nodes/alias]
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {o.ln.remote-nodes/pubkey
                        (fn [this value]
                          (let [{:ui/keys [current-rows] :as props} (comp/props this)]
                            (log/info :Report/formatting-name {:value value :props props})
                            (if-let [row (first (filter #(= (o.ln.remote-nodes/pubkey %) value) current-rows))]
                              (do
                                (log/info :Report/row {:row row})
                                (let [{::m.ln.remote-nodes/keys [id pubkey]} row]
                                  (u.links/ui-remote-node-link
                                   {o.ln.remote-nodes/id     id
                                    o.ln.remote-nodes/pubkey pubkey})))
                              (dom/p {} "not found"))))}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions      [delete-action]
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.remote-nodes/index
   ro/title            "Remote Nodes"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["remote-nodes"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["remote-node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Remote Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Remote Node"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
