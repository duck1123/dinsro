^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.remote-nodes-lj-notebook
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln.remote-nodes-lj :as a.ln.remote-nodes-lj]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Remote Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## get-node-info

(comment

  (<!! (a.ln.remote-nodes-lj/get-node-info n.lnd/node "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544"))
  (<!! (a.ln.remote-nodes-lj/get-node-info n.lnd/node "02e21b44ba07591e43aa59a29f8631edb299d306d232a51a38f28d3892751dc13d"))

  nil)
