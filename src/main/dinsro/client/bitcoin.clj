(ns dinsro.client.bitcoin
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.specs :as ds]
   [expound.alpha :as expound]
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
   (let [client-info {:url (:http/url client)}
         log-data    {:method method :args args :client-info client-info}]
     (log/debug :handle-request/starting log-data)
     (let [{:keys [error result]} (client/call client method args)]
       (log/debug :handle-request/finished (merge {:error error :result result} log-data))
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

(>def ::difficulty number?)
(>def ::pruned boolean?)
(>def ::size_on_disk number?)
(>def ::initialblockdownload boolean?)
(>def ::bestblockhash string?)
(>def ::verificationprogress number?)
(>def ::warnings string?)
(>def ::headers number?)
(>def ::softforks (s/keys))
(>def ::chainwork string?)
(>def ::chain string?)
(>def ::blocks number?)
(>def ::mediantime number?)
(>def ::blockchain-info
      (s/keys
       :req-un [::pruned ::difficulty
                ::size_on_disk
                ::initialblockdownload
                ::bestblockhash
                ::verificationprogress
                ::warnings
                ::headers
                ::softforks
                ::chainwork
                ::chain
                ::blocks
                ::mediantime]))

(comment

  (ds/gen-key ::blockchain-info)

  nil)

(>defn get-blockchain-info
  [client]
  [::s.client/config => ::blockchain-info]
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

(>def ::strippedsize number?)
(>def ::hash string?)
(>def ::versionHex string?)
(>def ::time number?)
(>def ::bits string?)
(>def ::tx-item string?)
(>def ::merkleroot string?)
(>def ::size number?)
(>def ::confirmations number?)
(>def ::tx (s/coll-of ::tx-item))
(>def ::weight number?)
(>def ::nTx number?)
(>def ::version number?)
(>def ::block-data
      (s/keys :req-un [::strippedsize
                       ::hash
                       ::versionHex
                       ::difficulty
                       ::time
                       ::merkleroot
                       ::bits
                       ::size
                       ::confirmations
                       ::tx
                       ::weight
                       ::chainwork
                       ::nTx
                       ::version]))

(comment

  (def example-block
    {:strippedsize  285,
     :hash          "0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206",
     :versionHex    "00000001",
     :difficulty    4.656542373906925E-10,
     :time          1296688602,
     :merkleroot    "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
     :bits          "207fffff",
     :size          285,
     :confirmations 1,
     :tx            ["4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"],
     :weight        1140,
     :chainwork     "0000000000000000000000000000000000000000000000000000000000000002",
     :nTx           1,
     :version       1,
     :nonce         2,
     :height        0,
     :mediantime    1296688602})

  (expound/expound ::block-data example-block)

  (ds/gen-key ::block-data)

  nil)

(>defn fetch-block-by-hash
  [client hash]
  [::s.client/config string? => ::block-data]
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
