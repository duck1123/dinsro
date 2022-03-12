(ns dinsro.client.bitcoin
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [farseer.client :as client]
   [farseer.spec.client :as s.client]
   [lambdaisland.glogc :as log]))

(defn get-client
  "Create a farseer client to a node"
  [config]
  (log/debug :client/creating {:config config})
  (client/make-client config))

(>defn handle-request
  "Invoke an RPC method on the client and return the results"
  ([client method]
   [::s.client/config keyword? => any?]
   (handle-request client method []))
  ([client method args]
   [::s.client/config keyword? vector? => any?]
   (do
     (log/debug :request/starting {:method method :args args :client client})
     (let [{:keys [error result]} (client/call client method args)]
       (log/debug :request/finished {:error error :result result :client client})
       (if error
         (throw (RuntimeException. (pr-str error)))
         result)))))

(>defn get-transaction
  "Fetch a tx specified by its txid"
  [client tx-id]
  [::s.client/config string? => any?]
  (handle-request client :gettransaction [tx-id true]))

(>defn get-raw-transaction
  [client tx-id]
  [::s.client/config string? => any?]
  (handle-request client :getrawtransaction [tx-id true]))

(>defn get-wallet-info
  [client]
  [::s.client/config => any?]
  (handle-request client :getwalletinfo))

(defn get-new-address
  [client]
  (handle-request client :getnewaddress ["" "bech32"]))

(defn list-transactions
  [client]
  (handle-request client :listtransactions))

(defn create-wallet
  [client wallet-name]
  (handle-request client :createwallet [wallet-name]))

(>defn get-blockchain-info
  [client]
  [::s.client/config => any?]
  (handle-request client :getblockchaininfo))

(>defn verify-message
  [client address signature message]
  [::s.client/config string? string? string? => any?]
  (handle-request client :verifymessage [address signature message]))

(>defn generate-to-address
  [client address]
  [::s.client/config string? => any?]
  (log/info :blocks/generating-to-address {:address address})
  (let [n 100]
    (log/spy (handle-request client :generatetoaddress [n address]))))

(>defn fetch-block-by-hash
  [client hash]
  [::s.client/config string? => any?]
  (handle-request client :getblock [hash]))

(>defn get-block-hash
  [client height]
  [::s.client/config number? => any?]
  (handle-request client :getblockhash [height]))

(>defn fetch-block-by-height
  [client height]
  [::s.client/config number? => any?]
  (let [hash (get-block-hash client height)]
    (fetch-block-by-hash client hash)))

(>defn fetch-tx
  [client tx-id]
  [::s.client/config string? => any?]
  (handle-request client :getrawtransaction [tx-id true]))

(>defn add-node
  "Add a peer connection to a given uri"
  [client node-uri]
  [::s.client/config string? => any?]
  (handle-request client :addnode [node-uri "add"]))

(>defn get-peer-info
  "Returns data about each connected network node as a json array of objects.

see: https://developer.bitcoin.org/reference/rpc/getpeerinfo.html"
  [client]
  [::s.client/config => any?]
  (handle-request client :getpeerinfo))

(>defn disconnect-node
  "Immediately disconnects from the specified peer node.

see: https://developer.bitcoin.org/reference/rpc/disconnectnode.html"
  [client address]
  [::s.client/config string? => nil?]
  (handle-request client :disconnectnode [address]))
