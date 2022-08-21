^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.nodes-notebook
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clj-commons.byte-streams :as bs]
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## initialize!

(comment

  n.lnd/node

  (:mnemonic n.lnd/node)

  (.genSeed (.unlocker n.lnd/client))

  (c.lnd-s/await-throwable (.genSeed n.lnd/client))

  (a.ln.nodes/download-cert! n.lnd/node)
  (a.ln.nodes/download-macaroon! n.lnd/node)

  (a.ln.nodes/get-client-s n.lnd/node)

  (a.ln.nodes/new-address-s n.lnd/node)

  (a.ln.nodes/get-info n.lnd/node)

  (def f (a.ln.nodes/initialize!-s n.lnd/node))

  f

  (.toStringUtf8 f)

  (<!! (cs/await-future f))

  nil)

(comment
  (a.ln.nodes/download-cert! (first (q.ln.nodes/index-ids)))

  n.lnd/client

  (.unlocker n.lnd/client)

  (def a (a.ln.nodes/new-address-s n.lnd/node))
  a
  (.value a)
  (a.ln.nodes/new-address-str n.lnd/node)

  (a.ln.nodes/unlock-sync!-s n.lnd/node)

  (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id n.lnd/node)))

  (q.ln.nodes/index-ids)

  (a.ln.nodes/delete-cert n.lnd/node)
  (a.ln.nodes/has-cert? n.lnd/node)
  (a.ln.nodes/download-cert! n.lnd/node)
  (a.ln.nodes/download-macaroon! n.lnd/node)

  (a.ln.nodes/delete-macaroon n.lnd/node)
  (a.ln.nodes/has-macaroon? n.lnd/node)

  (prn (slurp (a.ln.nodes/download-macaroon! n.lnd/node)))

  (println (a.ln.nodes/get-macaroon-text n.lnd/node))
  (a.ln.nodes/get-remote-instance n.lnd/node)

  (def client (a.ln.nodes/get-client-s n.lnd/node))
  client

  (.unlockWallet client a.ln.nodes/default-passphrase)

  (a.ln.nodes/get-info n.lnd/node)

  (a.c.nodes/generate-to-address!
   n.lnd/core-node-alice
   (a.ln.nodes/new-address-str n.lnd/node-alice))

  (q.ln.peers/index-records)

  (a.ln.nodes/get-macaroon-hex n.lnd/node)

  (m.ln.nodes/cert-file n.lnd/node)
  (a.ln.nodes/get-cert-text n.lnd/node)
  (def f (m.ln.nodes/macaroon-file (::m.ln.nodes/id n.lnd/node)))

  (.exists f)

  (def response (c.lnd-s/get-info client))

  (cs/->record response)
  (.alias response)

  (bytes->hex (bs/to-byte-array f))
  nil)
