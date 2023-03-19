^{:nextjournal.clerk/visibility {:code :hide}}
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
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def message "this is a test message")
(def signature "dh5bb37x188z6ccqq8ijsjnwa9wbugckicnjzqa73fjb4osegzsdnnejxip76oagetuwknicpktzk6jubpg59bho3r1maew78uudizqw")

;; ## get-client

(try
  (pr-str n.lnd/node)
  (catch Exception ex ex))

;; ## get-client


(def client (a.ln.nodes/get-client n.lnd/node))

;; ## get-macaroon-text

(try
  (a.ln.nodes/get-macaroon-text n.lnd/node)
  (catch Exception ex ex))

;; ## initialize-s!

(comment

  (def f (a.ln.nodes/initialize! n.lnd/node))

  f

  (.toStringUtf8 f)

  (<!! (cs/await-future f))

  nil)

;; ## download-cert!

(comment

  (a.ln.nodes/download-cert! n.lnd/node)

  nil)

;; ## download-macaroon

(comment

  (a.ln.nodes/download-macaroon! n.lnd/node)

  nil)

;; ## delete-cert

(comment

  (a.ln.nodes/delete-cert n.lnd/node)

  nil)

;; ## get-info

(comment

  (a.ln.nodes/get-info n.lnd/node)

  (def response (c.lnd-s/get-info client))

  (cs/->record response)
  (.alias response)

  nil)

;; ## other


(try
  (a.ln.nodes/has-cert? n.lnd/node)
  (catch Exception ex ex))

(a.ln.nodes/get-cert-text n.lnd/node)

;; ## initialize!

(comment

  (a.ln.nodes/initialize! n.lnd/node)
  (a.ln.nodes/get-info n.lnd/node)

  nil)

(comment
  n.lnd/node

  (:mnemonic n.lnd/node)

  (.genSeed (.unlocker n.lnd/client))

  (c.lnd-s/await-throwable (.genSeed n.lnd/client))

  (a.ln.nodes/new-address n.lnd/node)

  n.lnd/client

  (.unlocker n.lnd/client)

  (def a (a.ln.nodes/new-address n.lnd/node))
  a
  (.value a)
  (a.ln.nodes/new-address-str n.lnd/node)

  (a.ln.nodes/unlock! n.lnd/node)

  (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id n.lnd/node)))

  (q.ln.nodes/index-ids)

  (a.ln.nodes/has-cert? n.lnd/node)
  (a.ln.nodes/download-macaroon! n.lnd/node)

  (a.ln.nodes/delete-macaroon n.lnd/node)
  (a.ln.nodes/has-macaroon? n.lnd/node)

  (prn (slurp (a.ln.nodes/download-macaroon! n.lnd/node)))

  (.unlockWallet client a.ln.nodes/default-passphrase)

  (a.c.nodes/generate-to-address!
   n.lnd/core-node-alice
   (a.ln.nodes/new-address-str n.lnd/node-alice))

  (q.ln.peers/index-records)

  (a.ln.nodes/get-macaroon-hex n.lnd/node)

  (m.ln.nodes/cert-file n.lnd/node)
  (a.ln.nodes/get-cert-text n.lnd/node)
  (def f (m.ln.nodes/macaroon-file (::m.ln.nodes/id n.lnd/node)))

  (.exists f)

  (bytes->hex (bs/to-byte-array f))
  nil)
