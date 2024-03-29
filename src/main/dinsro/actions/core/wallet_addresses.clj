(ns dinsro.actions.core.wallet-addresses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.addresses :as a.c.addresses]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.queries.core.addresses :as q.c.addresses]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.crypto.ExtPublicKey))

;; [[../../model/core/wallet_addresses.cljc]]
;; [[../../mutations/core/wallet_addresses.cljc]]
;; [[../../ui/admin/core/wallet_addresses.cljs]]

(>defn register-address!
  [wallet address path-index]
  [::m.c.wallets/item ::m.c.addresses/address ::m.c.wallet-addresses/path-index => any?]
  (let [{::m.c.wallets/keys [id]} wallet]
    (log/info :register-address! {:id id :address address :path-index path-index})
    (let [wallet-address-id (q.c.wallet-addresses/find-by-wallet-and-index id path-index)]
      (if wallet-address-id
        (do
          (log/trace :register-address!/found {})
          wallet-address-id)
        (do
          (log/trace :register-address!/not-found {})
          (let [address-id (a.c.addresses/register-address! address)]
            (q.c.wallet-addresses/create-record
             {::m.c.wallet-addresses/address    address-id
              ::m.c.wallet-addresses/wallet     id
              ::m.c.wallet-addresses/path-index path-index})))))))

(>defn generate!
  [{wallet-id  ::m.c.wallet-addresses/wallet
    address-id ::m.c.wallet-addresses/address
    :as        props}]
  [::m.c.wallet-addresses/item => any?]
  (log/info :generate!/starting {:address-id address-id
                                 :wallet-id  wallet-id
                                 :props      props})
  (if-let [wallet (q.c.wallets/read-record wallet-id)]
    (if-let [address-record (q.c.addresses/read-record address-id)]
      (if-let [network-id (::m.c.wallets/network wallet)]
        (if-let [node-id (first (q.c.nodes/find-by-network network-id))]
          (if-let [node (q.c.nodes/read-record node-id)]
            (let [client (a.c.node-base/get-client node)
                  address (::m.c.addresses/address address-record)]
              (log/info :generate!/starting {:client client :address address})
              (c.bitcoin-s/generate-to-address! client address))
            (throw (ex-info "Failed to find node" {})))
          (throw (ex-info "Failed to find node id" {})))
        (throw (ex-info "no network id" {})))
      (throw (ex-info "no address record" {})))
    (throw (ex-info "Failed to find wallet" {}))))

(defn calculate-address!
  [wallet index]
  (log/info :calculate-address!/starting {:wallet wallet :index index})
  (let [ext-public-key  (::m.c.wallets/ext-public-key wallet)
        ext-public-key2 ^ExtPublicKey (ExtPublicKey/fromString ext-public-key)]
    (log/info :calculate-addresses!/read-key {:ext-public-key  ext-public-key
                                              :ext-public-key2 ext-public-key2})
    (let [wallet-path (::m.c.wallets/derivation wallet)
          child-path  (str "/0/" index)
          ext-pub-key (c.bitcoin-s/get-child-key-pub ext-public-key2
                                                     wallet-path
                                                     (str wallet-path child-path))]
      (log/info :calculate-addresses!/a {:ext-pub-key ext-pub-key})
      (let [script-pub-key (c.bitcoin-s/get-script-pub-key ext-pub-key)]
        (log/info :calculate-addresses!/b {:script-pub-key script-pub-key})
        (let [network "regtest"]
          (log/info :calculate-addresses!/c {:network network})
          (let [address (c.bitcoin-s/get-address script-pub-key network)]
            (log/info :calculate-addresses!/d {:address address})
            (register-address! wallet address index)))))))

;; bash-4.4# lncli -n regtest newaddress p2wkh
;; {
;;  "address" : "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g"
;;  }

(defn calculate-addresses!
  [wallet-id]
  (log/info :calculate-addresses!/starting {:wallet-id wallet-id})
  (if-let [wallet (q.c.wallets/read-record wallet-id)]
    (dotimes [n 20]
      (calculate-address! wallet n))
    (do
      (log/error :calculate-addresses!/no-wallet {:wallet-id wallet-id})
      (throw (ex-info "Failed to find wallet" {})))))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id}))
