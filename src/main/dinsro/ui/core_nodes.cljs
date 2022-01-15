(ns dinsro.ui.core-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.mutations.core-nodes :as mu.core-nodes]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defn connect-action
  [report-instance {::m.core-nodes/keys [id]}]
  (comp/transact! report-instance [(mu.core-nodes/connect! {::m.core-nodes/id id})]))

(def connect-button
  {:label     "Connect"
   :action    connect-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(form/defsc-form CoreNodeForm [_this _props]
  {fo/id           m.core-nodes/id
   fo/attributes   [m.core-nodes/name
                    m.core-nodes/host
                    m.core-nodes/port
                    m.core-nodes/rpcuser
                    m.core-nodes/rpcpass
                    m.core-nodes/balance
                    m.core-nodes/tx-count
                    m.core-nodes/chain]
   fo/cancel-route ["core-nodes"]
   fo/route-prefix "core-node"
   fo/title        "Core Node"})

(report/defsc-report CoreNodesReport
  [_this _props]
  {ro/column-formatters {::m.core-nodes/name (fn [this name {::m.core-nodes/keys [id]}]
                                               (dom/a {:onClick #(form/edit! this CoreNodeForm id)} name))}
   ro/columns           [m.core-nodes/name
                         m.core-nodes/balance
                         m.core-nodes/tx-count
                         m.core-nodes/chain]
   ro/row-actions       [connect-button]
   ro/source-attribute  ::m.core-nodes/all-nodes
   ro/title             "Core Node Report"
   ro/row-pk            m.core-nodes/id
   ro/run-on-mount?     true
   ro/route             "core-nodes"})
