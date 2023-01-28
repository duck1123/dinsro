(ns dinsro.ui.ln.remote-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.remote-nodes :as j.ln.remote-nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.remote-node-peers :as u.ln.remote-node-peers]
   [lambdaisland.glogc :as log]))

(defsc ShowRemoteNode
  [_this {:ui/keys                 [peers]
          ::m.ln.remote-nodes/keys [id pubkey]}]
  {:route-segment ["remote-nodes" :id]
   :ident         ::m.ln.remote-nodes/id
   :query         [::m.ln.remote-nodes/id
                   ::m.ln.remote-nodes/pubkey
                   {:ui/peers (comp/get-query u.ln.remote-node-peers/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.ln.remote-nodes/id     nil
                   ::m.ln.remote-nodes/pubkey ""
                   :ui/peers                  {}}
   :pre-merge     (u.links/page-merger
                   ::m.ln.remote-nodes/id
                   {:ui/peers u.ln.remote-node-peers/SubPage})
   :will-enter    (partial u.links/page-loader ::m.ln.remote-nodes/id ::ShowRemoteNode)}
  (dom/div {}
    (dom/div {:classes [:.ui.segment]}
      (dom/h1 {} "Remote Node")
      (dom/p {} "id: " (pr-str id))
      (dom/p {} "pubkey: " (str pubkey)))
    (u.ln.remote-node-peers/ui-sub-page peers)))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.remote-nodes/pubkey
                        m.ln.remote-nodes/alias]
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.ln.remote-nodes/pubkey (fn [this value]
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
   ro/route            "remote-nodes"
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.remote-nodes/index
   ro/title            "Remote Nodes"})
