^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.bitcoin-s-notebook
  (:require
   [clojure.core.async :as async]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResultPostV19
   scala.Option))

;; # Scala Bitcoin Client

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def client
  (-> "bitcoin-alice"
      q.c.nodes/find-id-by-name
      q.c.nodes/read-record
      a.c.node-base/get-client))

;; ## get entropy

(c.bitcoin-s/get-entropy)

;; ## create mnemonic words

(c.bitcoin-s/create-mnemonic-words)

;; ## Networks

;; ### regtest

(c.bitcoin-s/regtest-network)

;; ## get blockchain info result [link](https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#getBlockChainInfo:scala.concurrent.Future[org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResult])

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(def get-blockchain-info-result
  (let [chain                   (c.bitcoin-s/regtest-network)
        blocks                  (int 1)
        headers                 (int 1)
        bestblockhash           nil
        difficulty              nil
        mediantime              (int 1)
        verification-progress   nil
        initial-block-download? true
        chainwork               ""
        size-on-disk            1
        pruned?                 true
        pruneheight             (Option/apply (int 1))
        softforks               nil
        warnings                ""]
    (GetBlockChainInfoResultPostV19.
     chain
     blocks
     headers
     bestblockhash
     difficulty
     mediantime
     verification-progress
     initial-block-download?
     chainwork
     size-on-disk
     pruned?
     pruneheight
     softforks
     warnings)))

get-blockchain-info-result

^:nextjournal.clerk/no-cache
(nu/display2 (cs/->record get-blockchain-info-result))

(:result (async/<!! (c.bitcoin-s/get-block-hash client 0)))
