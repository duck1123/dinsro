(ns dinsro.ui.ln.remote-nodes
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]))

(form/defsc-form RemoteNodeForm
  [_this _props]
  {fo/id            m.ln.tx/id
   fo/attributes    [m.ln.remote-nodes/pubkey]
   fo/route-prefix  "remote-node"
   fo/title         "Remote Node"})

(report/defsc-report RemoteNodesReport
  [_this _props]
  {ro/columns          [m.ln.remote-nodes/pubkey
                        m.ln.remote-nodes/alias]
   ro/route            "remote-nodes"
   ro/row-actions      []
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.remote-nodes/index
   ro/title            "Remote Nodes"})
