(ns dinsro.actions.core.wallet-addresses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [lambdaisland.glogc :as log]))

(>defn register-address!
  [wallet address path-index]
  [::m.c.wallets/item ::m.c.wallet-addresses/address ::m.c.wallet-addresses/path-index => any?]
  (let [{::m.c.wallets/keys [id]} wallet]
    (log/info :register-address! {:id id :address address :path-index path-index})
    (let [address-id (q.c.wallet-addresses/find-by-wallet-and-index id path-index)]
      (if address-id
        (do
          (log/finer :register-address!/found {})
          address-id)
        (do
          (log/finer :register-address!/not-found {})
          (q.c.wallet-addresses/create-record
           {::m.c.wallet-addresses/address    address
            ::m.c.wallet-addresses/wallet     id
            ::m.c.wallet-addresses/path-index path-index}))))))

(>defn register-addresses!
  [wallet n]
  [::m.c.wallets/item ::m.c.wallet-addresses/path-index => any?]
  (log/info :register-addresses!/starting {:wallet wallet :n n})
  (dotimes [index n]
    (let [address (a.c.wallets/get-address wallet index)]
      (register-address! wallet address index))))

(defn generate!
  [{wallet-id                   ::m.c.wallet-addresses/wallet
    ::m.c.wallet-addresses/keys [address]}]
  [::m.c.wallet-addresses/item => any?]
  (log/info :generate!/starting {:address address :wallet-id wallet-id})
  (if-let [wallet (q.c.wallets/read-record wallet-id)]
    (if-let [network-id (::m.c.wallets/network wallet)]
      (if-let [node-id (first (q.c.nodes/find-by-network network-id))]
        (if-let [node (q.c.nodes/read-record node-id)]
          (let [client (a.c.node-base/get-client node)]
            (c.bitcoin-s/generate-to-address! client address))
          (throw (RuntimeException. "Failed to find node")))
        (throw (RuntimeException. "Failed to find node id")))
      (throw (RuntimeException. "no network id")))
    (throw (RuntimeException. "Failed to find wallet"))))
