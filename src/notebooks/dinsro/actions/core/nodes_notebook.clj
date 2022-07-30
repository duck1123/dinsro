^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.nodes-notebook
  (:require
   [clojure.core.async :as async]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.scala :as cs]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## Setup users

;; Alice


^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))

;; Bob

^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(def node node-alice)

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(def node-id (::m.c.nodes/id node))

(def client (a.c.node-base/get-client node))

;; ## Generate to address

(def address "bcrt1qpe5thhr9p582w0ymg99ea5ql9e5y5lqsxm5527")
(def address2 "bcrt1qasndfxduf6ap94hkpn3kde0mcvdrqvs95e8adz")

(comment

  (dotimes [_a 100] (a.c.nodes/generate-to-address! node address))

  nil)

;; ## Get Blockchain info

;; ^{::clerk/viewer clerk/code}
;; (cs/->record (c.bitcoin-s/get-blockchain-info client))

(comment
  (tap> (q.c.nodes/index-records))

  (c.bitcoin-s/list-transactions client)

  (a.c.nodes/fetch! node)

  (c.bitcoin-s/list-transactions client)

  (a.c.nodes/fetch-transactions! node)

  (q.c.peers/index-ids)

  (a.c.node-base/get-auth-credentials node)

  (a.c.node-base/get-rpc-uri node)
  (a.c.node-base/get-remote-uri node)

  (c.bitcoin-s/regtest-network)

  (a.c.node-base/get-remote-instance node)

  (def executor (cs/get-executor))
  (def context (cs/get-execution-context executor))

  (def client-s (a.c.node-base/get-client node))
  client-s

  (c.bitcoin-s/get-peer-info client-s)

  (cs/await-future (.getBlockCount client-s) context)

  (def ch (cs/await-future (.getPeerInfo client-s)))
  (async/<!! (cs/await-future (.getPeerInfo client-s) context))

  nil)
