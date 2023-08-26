(ns dinsro.ui.ln.remote-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
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
   [dinsro.ui.ln.remote-nodes.peers :as u.ln.rn.peers]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/ln/remote_nodes.cljc]]
;; [[../../model/ln/remote_nodes.cljc]]

(def index-page-id :ln-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def parent-router-id :ln)
(def required-role :user)
(def show-page-key :ln-remote-nodes-show)

(def debug-show false)
(def debug-show-page false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.ln.remote-nodes/delete!))

(defsc Show
  [_this {::m.ln.remote-nodes/keys [id pubkey]
          :ui/keys                 [peers]
          :as                      props}]
  {:ident         ::m.ln.remote-nodes/id
   :initial-state {::m.ln.remote-nodes/id     nil
                   ::m.ln.remote-nodes/pubkey ""
                   :ui/peers                  {}}
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/peers [u.ln.rn.peers/SubPage {}]})
   :query         [::m.ln.remote-nodes/id
                   ::m.ln.remote-nodes/pubkey
                   {:ui/peers (comp/get-query u.ln.rn.peers/SubPage)}
                   [df/marker-table '_]]}
  (log/debug :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Remote Node")
        (dom/p {} "id: " (pr-str id))
        (dom/p {} "pubkey: " (str pubkey)))
      (if peers
        (u.ln.rn.peers/ui-sub-page peers)
        (ui-segment {:color "red" :inverted true}
          "Failed to load peers"))
      (when debug-show
        (u.debug/log-props props)))
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.remote-nodes/pubkey (fn [this value]
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
   ro/columns           [m.ln.remote-nodes/pubkey
                         m.ln.remote-nodes/alias]
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "remote-nodes"
   ro/row-actions       [delete-action]
   ro/row-pk            m.ln.remote-nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.remote-nodes/index
   ro/title             "Remote Nodes"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["remote-nodes"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.ln.remote-nodes/keys [id]
          ::m.navlinks/keys        [target]
          :as                      props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.ln.remote-nodes/id nil
                   ::m.navlinks/id        show-page-key
                   ::m.navlinks/target    {}}
   :query         [::m.ln.remote-nodes/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["remote-nodes" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (dom/div {}
      (if target
        (ui-show target)
        (ui-segment {:color "red" :inverted true}
          "Failed to load record"))
      (when debug-show-page
        (u.debug/log-props props)))
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Remote Node"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
