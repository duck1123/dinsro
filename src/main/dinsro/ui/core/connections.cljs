(ns dinsro.ui.core.connections
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.connections :as m.c.connections]))

(report/defsc-report CoreNodeConnectionsReport
  [_this _props]
  {ro/columns          [m.c.connections/host]
   ro/source-attribute ::m.c.nodes/index
   ro/run-on-mount?    true
   ro/title            "Core Node Connections"
   ro/route            "core-node-connections"
   ro/row-pk           m.c.connections/id})
