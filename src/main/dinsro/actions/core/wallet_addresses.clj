(ns dinsro.actions.core.wallet-addresses
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [taoensso.timbre :as log]))

(defn register-address!
  [wallet address path-index]
  (let [{::m.c.wallets/keys [id]} wallet]
    (log/info :register-address! {:id id :address address :path-index path-index})
    (let [address-id (q.c.wallet-addresses/find-by-wallet-and-index id path-index)]
      (if address-id
        (log/info :register-address!/found {})
        (do
          (log/info :register-address!/not-found {})
          (q.c.wallet-addresses/create-record
           {::m.c.wallet-addresses/address    address
            ::m.c.wallet-addresses/wallet     id
            ::m.c.wallet-addresses/path-index path-index}))))))

(defn register-addresses!
  [wallet n]
  (log/info :register-addresses!/starting {:wallet wallet :n n})
  (dotimes [index n]
    (let [address (a.c.wallets/get-address wallet index)]
      (register-address! wallet address index))))

(defn generate!
  [{wallet-id                   ::m.c.wallet-addresses/wallet
    ::m.c.wallet-addresses/keys [address]}]
  (log/infof "generate: %s" address)
  (if-let [wallet (q.c.wallets/read-record wallet-id)]
    (let [node-id (::m.c.wallets/node wallet)]
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [client (m.c.nodes/get-client node)]
          (c.bitcoin/generate-to-address client address))
        (throw (RuntimeException. "Failed to find node"))))
    (throw (RuntimeException. "Failed to find wallet"))))
