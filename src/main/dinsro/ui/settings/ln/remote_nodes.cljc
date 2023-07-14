(ns dinsro.ui.settings.ln.remote-nodes
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
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.settings.ln.remote-nodes.peers :as u.s.ln.rn.peers]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-ln-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def show-page-key :settings-ln-remote-nodes-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.s.ln.rn.peers/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {:ui/keys                 [peers router]
          ::m.ln.remote-nodes/keys [id pubkey]}]
  {:ident         ::m.ln.remote-nodes/id
   :initial-state {::m.ln.remote-nodes/id     nil
                   ::m.ln.remote-nodes/pubkey ""
                   :ui/peers                  {}
                   :ui/router                 {}}
   :pre-merge     (u.loader/page-merger ::m.ln.remote-nodes/id
                    {:ui/peers  [u.s.ln.rn.peers/SubPage {}]
                     :ui/router [Router {}]})
   :query         [::m.ln.remote-nodes/id
                   ::m.ln.remote-nodes/pubkey
                   {:ui/peers (comp/get-query u.s.ln.rn.peers/SubPage)}
                   {:ui/router (comp/get-query Router)}
                   [df/marker-table '_]]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Remote Node")
        (dom/p {} "id: " (pr-str id))
        (dom/p {} "pubkey: " (str pubkey)))
      (u.s.ln.rn.peers/ui-sub-page peers)
      (ui-router router))
    (ui-segment {} "Failed to load record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters
   {::m.ln.remote-nodes/pubkey
    (fn [this value]
      (let [{:ui/keys [current-rows] :as props} (comp/props this)]
        (log/info :Report/formatting-name {:value value :props props})
        (if-let [row (first (filter #(= (::m.ln.remote-nodes/pubkey %) value) current-rows))]
          (do
            (log/info :Report/row {:row row})
            (let [{::m.ln.remote-nodes/keys [id pubkey]} row]
              (u.links/ui-remote-node-link
               {::m.ln.remote-nodes/id     id
                ::m.ln.remote-nodes/pubkey pubkey})))
          (dom/p {} "not found"))))}
   ro/columns          [m.ln.remote-nodes/pubkey
                        m.ln.remote-nodes/alias]
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "remote-nodes"
   ro/row-actions      [(u.buttons/row-action-button "Delete" ::m.ln.remote-nodes/id mu.ln.remote-nodes/delete!)]
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.remote-nodes/index
   ro/title            "Remote Nodes"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["remote-nodes"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (u.debug/load-error props "settings show remote node")))

(m.navlinks/defroute   :settings-ln-remote-nodes
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Remote Nodes"
   ::m.navlinks/model-key     ::m.ln.remote-nodes/id
   ::m.navlinks/parent-key    :settings-ln
   ::m.navlinks/router        :settings-ln
   ::m.navlinks/required-role :user})
