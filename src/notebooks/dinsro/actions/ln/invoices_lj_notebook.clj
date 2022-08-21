^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.invoices-lj-notebook
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln.invoices-lj :as a.ln.invoices-lj]
   [dinsro.actions.ln.invoices-notebook :as n.a.ln.invoices]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Invoice Actions (LightningJ)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def inv n.a.ln.invoices/inv)
(def inv2 n.a.ln.invoices/inv2)

(comment
  (def client (a.ln.nodes-lj/get-client n.lnd/node))

  nil)

(comment
  (c.lnd/list-invoices (a.ln.nodes-lj/get-client n.lnd/node))

  (<!! (c.lnd/decode-pay-req client inv))

  (<!! (c.lnd/lookup-invoice client inv))

  (let [client (a.ln.nodes-lj/get-invoices-client n.lnd/node)]
    (<!! (c.lnd/lookup-invoice client inv)))

  (let [client (a.ln.nodes-lj/get-client n.lnd/node-alice)]
    (<!! (c.lnd/send-payment-sync client inv2)))

  (a.ln.invoices-lj/fetch n.lnd/node)

  (def iresponse (async/<!! (a.ln.invoices-lj/fetch n.lnd/node)))
  (first (:invoices iresponse))

  nil)
