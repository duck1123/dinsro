(ns dinsro.actions.wallet-addresses
  (:require
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.wallets :as q.wallets]
   [dinsro.queries.wallet-addresses :as q.wallet-addresses]
   [taoensso.timbre :as log]))

(defn generate!
  [{wallet-id                 ::m.wallet-addresses/wallet
    ::m.wallet-addresses/keys [address]}]
  (log/infof "generate: %s" address)
  (if-let [wallet (q.wallets/read-record wallet-id)]
    (let [node-id (::m.wallets/node wallet)]
      (if-let [node (q.core-nodes/read-record node-id)]
        (let [client (m.core-nodes/get-client node)]
          (c.bitcoin/generate-to-address client address))
        (throw (RuntimeException. "Failed to find node"))))
    (throw (RuntimeException. "Failed to find wallet"))))

(comment

  (q.wallets/index-ids)

  (q.wallet-addresses/find-by-wallet (first (q.wallets/index-ids)))

  (q.wallet-addresses/index-ids)

  (q.wallet-addresses/index-records)

  (def address "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (a.nbxplorer/track-address address)
  (a.nbxplorer/get-transactions-for-address address)

  nil)
