^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.nodes-notebook
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clj-commons.byte-streams :as bs]
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def user-alice (q.users/find-eid-by-name "alice"))
(def alice-id (q.users/find-eid-by-name "alice"))
(def user-bob (q.users/find-eid-by-name "bob"))
(def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name alice-id "lnd-alice")))
(def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
(def node node-alice)

;; ## initialize!

(comment

  (a.ln.nodes/initialize!-s node)

  nil)

(comment
  (a.ln.nodes/download-cert! (first (q.ln.nodes/index-ids)))


  user-alice
  user-bob

  node-alice
  node-bob
  node

  (def core-node-alice (q.c.nodes/read-record (q.c.nodes/find-by-ln-node (::m.ln.nodes/id node-alice))))
  (def core-node-bob (q.c.nodes/read-record (q.c.nodes/find-by-ln-node (::m.ln.nodes/id node-bob))))

  core-node-alice

  (def a (a.ln.nodes/new-address-s node))
  a
  (.value a)
  (a.ln.nodes/new-address-str node)

  (a.ln.nodes/unlock-sync!-s node)

  (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id node)))

  (def client1 (a.ln.nodes/get-client node))
  client1

  (with-open [client (a.ln.nodes/get-client node)] (c.lnd/list-invoices client))
  (c.lnd/list-payments client1)

  (q.ln.nodes/index-ids)
  (a.ln.nodes/initialize! node)

  (a.ln.nodes/generate! node-alice)

  (a.ln.nodes/update-info! node)

  (a.ln.nodes/delete-cert node)
  (a.ln.nodes/has-cert? node)
  (a.ln.nodes/download-cert! node)
  (a.ln.nodes/download-macaroon! node)

  (a.ln.nodes/delete-macaroon node)
  (a.ln.nodes/has-macaroon? node)

  (prn (slurp (a.ln.nodes/download-macaroon! node)))

  (println (a.ln.nodes/get-macaroon-text node))
  (a.ln.nodes/get-remote-instance node)

  (def client (a.ln.nodes/get-client-s node))
  client

  (.unlockWallet client a.ln.nodes/default-passphrase)

  (a.ln.nodes/get-info node)

  (a.c.nodes/generate-to-address! core-node-alice (a.ln.nodes/new-address-str node-alice))

  (<!! (a.ln.nodes/initialize! node))

  (q.ln.peers/index-records)

  (a.ln.nodes/new-address node (fn [response] response))

  (a.ln.nodes/get-client node)
  (a.ln.nodes/get-macaroon-hex node)

  (m.ln.nodes/cert-file node)
  (a.ln.nodes/get-cert-text node)
  (def f (m.ln.nodes/macaroon-file (::m.ln.nodes/id node)))

  (.exists f)

  (def response (c.lnd-s/get-info client))

  (cs/->record response)
  (.alias response)

  (bytes->hex (bs/to-byte-array f))
  nil)
