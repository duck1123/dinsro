(ns dinsro.actions.ln.accounts
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.ln.accounts :as q.ln.accounts]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.ln.accounts/delete! id))

(defn fetch!
  [node]
  (let [node-id (::m.ln.nodes/id node)]
    (log/info :fetch!/starting {:node-id node-id})
    (let [client (a.ln.nodes/get-client node)
          response (c.lnd-s/list-wallet-accounts client)
          record (cs/->record response)]
      (log/info :fetch!/finished {:response response
                                  :record record})
      record)))

(defn process-fetched-account!
  [user-id network-id node-id account]
  (log/info :process-fetched-account!/starting {:user-id    user-id
                                                :network-id network-id
                                                :node-id    node-id
                                                :account    account})
  (let [node                       (q.ln.nodes/read-record node-id)
        node-name                  (::m.ln.nodes/name node)
        {:dinsro.client.converters.account/keys
         [address-type derivation-path extended-public-key
          master-key-fingerprint]} account
        wallet-id                  (q.c.wallets/create-record
                                    {::m.c.wallets/name           (str node-name " - " derivation-path)
                                     ::m.c.wallets/derivation     derivation-path
                                     ::m.c.wallets/ext-public-key extended-public-key
                                     ::m.c.wallets/network        network-id
                                     ::m.c.wallets/user           user-id})]
    (log/info :process-fetched-account!/wallet-created {:wallet-id wallet-id})
    (let [params
          {::m.ln.accounts/address-type           (:dinsro.client.converters.address-type/name address-type)
           ::m.ln.accounts/master-key-fingerprint master-key-fingerprint
           ::m.ln.accounts/wallet                 wallet-id
           ::m.ln.accounts/node                   node-id}]
      (log/info :process-fetched-account!/prepared {:params params})
      (let [id (q.ln.accounts/create-record params)]
        (log/info :process-fetched-account!/finished {:id id})
        id))))

(defn do-fetch-accounts!
  [node-id]
  (log/info :do-fetch-accounts!/starting {:node-id node-id})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (if-let [network-id (::m.ln.nodes/network node)]
      (if-let [user-id (::m.ln.nodes/user node)]
        (do
          (log/info :do-fetch-accounts!/node-found {:node node})
          (let [record (fetch! node)]
            (log/info :do-fetch-accounts!/fetched {:record record})
            (let [accounts (:dinsro.client.converters.list-accounts-response/accounts  record)]
              (doseq [account accounts]
                (process-fetched-account! user-id network-id node-id account)))))
        (do
          (log/error :do-fetch-accounts!/user-not-found {:node node})
          (throw (ex-info "no user id" {}))))
      (do
        (log/error :do-fetch-accounts!/network-not-found {:node node})
        (throw (ex-info "no network id" {}))))
    (do
      (log/error :do-fetch-accounts!/node-not-found {:node-id node-id})
      {:status :fail})))
