^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.nodes-lj-notebook
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Node Actions (LightningJ)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (def client1 (a.ln.nodes-lj/get-client n.lnd/node))
  client1

  (with-open [client (a.ln.nodes-lj/get-client n.lnd/node)] (c.lnd/list-invoices client))
  (c.lnd/list-payments client1)

  (a.ln.nodes-lj/update-info! n.lnd/node)

  (a.ln.nodes-lj/initialize! n.lnd/node)

  (a.ln.nodes-lj/generate! n.lnd/node-alice)

  (<!! (a.ln.nodes-lj/initialize! n.lnd/node))

  (a.ln.nodes-lj/new-address n.lnd/node (fn [response] response))

  nil)
