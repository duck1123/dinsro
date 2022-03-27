(ns dinsro.actions.core.wallet-addresses
  (:require
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [taoensso.timbre :as log]))

(defn generate!
  [{wallet-id                 ::m.c.wallet-addresses/wallet
    ::m.c.wallet-addresses/keys [address]}]
  (log/infof "generate: %s" address)
  (if-let [wallet (q.c.wallets/read-record wallet-id)]
    (let [node-id (::m.c.wallets/node wallet)]
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [client (m.c.nodes/get-client node)]
          (c.bitcoin/generate-to-address client address))
        (throw (RuntimeException. "Failed to find node"))))
    (throw (RuntimeException. "Failed to find wallet"))))

(comment

  (tap> (q.c.wallets/index-ids))

  (q.c.wallet-addresses/find-by-wallet (first (q.c.wallets/index-ids)))

  (q.c.wallet-addresses/index-ids)

  (q.c.wallet-addresses/index-records)

  (def address "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (a.nbxplorer/track-address address)
  (a.nbxplorer/get-transactions-for-address address)

  nil)
