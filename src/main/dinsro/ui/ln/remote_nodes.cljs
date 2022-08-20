(ns dinsro.ui.ln.remote-nodes
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.mutations.ln.remote-nodes :as mu.ln.remote-nodes]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.remote-node-peers :as u.ln.remote-node-peers]
   [lambdaisland.glogc :as log]))

(form/defsc-form RemoteNodeForm
  [_this _props]
  {fo/id           m.ln.tx/id
   fo/attributes   [m.ln.remote-nodes/pubkey]
   fo/route-prefix "remote-node-form"
   fo/title        "Remote Node"})

(defn ShowRemoteNode-pre-merge
  [{:keys [data-tree state-map]}]
  (log/finer :ShowRemoteNode-pre-merge/starting {:data-tree data-tree :state-map state-map})
  (let [node-id (::m.ln.remote-nodes/id data-tree)]
    (log/finer :ShowNode/pre-merge-parsed {:node-id node-id})
    (let [peers-data   (u.links/merge-state state-map u.ln.remote-node-peers/SubPage {::m.ln.remote-nodes/id node-id})
          updated-data (-> data-tree (assoc :peers peers-data))]
      (log/finer :ShowRemoteNode-pre-merge/finished {:updated-data updated-data})
      updated-data)))

(defsc ShowRemoteNode
  [_this {:keys                    [peers]
          ::m.ln.remote-nodes/keys [id pubkey]}]
  {:route-segment ["remote-nodes" :id]
   :ident         ::m.ln.remote-nodes/id
   :query         [::m.ln.remote-nodes/id
                   ::m.ln.remote-nodes/pubkey
                   {:peers (comp/get-query u.ln.remote-node-peers/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.ln.remote-nodes/id     nil
                   ::m.ln.remote-nodes/pubkey ""
                   :peers                     {}}
   :pre-merge     ShowRemoteNode-pre-merge
   :will-enter
   (fn [app {id :id}]
     (let [id    (new-uuid id)
           ident [::m.ln.remote-nodes/id id]
           state (-> (app/current-state app) (get-in ident))]
       (log/info :ShowNode/will-enter {:app app :id id :ident ident})
       (dr/route-deferred
        ident
        (fn []
          (log/info :ShowRemoteNode/will-enter2
                    {:id       id
                     :state    state
                     :controls (control/component-controls app)})
          (df/load!
           app ident ShowRemoteNode
           {:marker               :ui/selected-node
            :target               [:ui/selected-node]
            :post-mutation        `dr/target-ready
            :post-mutation-params {:target ident}})))))}
  (dom/div {}
    (dom/div {:classes [:.ui.segment]}
      (dom/h1 {} "Remote Node")
      (dom/p {} "id: " (pr-str id))
      (dom/p {} "pubkey: " (str pubkey)))
    (u.ln.remote-node-peers/ui-remote-node-peers-sub-page peers)))

(def fetch-action-button
  "Delete button for reports"
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this {::m.ln.remote-nodes/keys [id] :as props}]
             (log/info :fetch-action-button/clicked {:id id :props props})
             (comp/transact! this [(mu.ln.remote-nodes/fetch! {::m.ln.remote-nodes/id id})]))})

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
   ro/row-actions      [fetch-action-button]
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.remote-nodes/index
   ro/title            "Remote Nodes"})
