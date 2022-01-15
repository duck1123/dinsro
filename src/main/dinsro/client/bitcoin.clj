(ns dinsro.client.bitcoin
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [farseer.client :as client]
   [farseer.spec.client :as s.client]
   [taoensso.timbre :as log]))

(defn get-client
  [config]
  (client/make-client config))

(>defn handle-request
  ([client method]
   [::s.client/config keyword? => any?]
   (handle-request client method []))
  ([client method args]
   [::s.client/config keyword? vector? => any?]
   (let [{:keys [error result]} (client/call client method args)]
     (if error
       (throw (RuntimeException. (pr-str error)))
       result))))

(>defn get-transaction
  [client tx-id]
  [::s.client/config string? => any?]
  (handle-request client :gettransaction [tx-id true]))

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
  (log/infof "generate to address: %s" address)
  (let [n 100]
    (log/spy :info (handle-request client :generatetoaddress [n address]))))

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
  [client node-uri]
  [::s.client/config string? => any?]
  (handle-request client :addnode [node-uri "add"]))

(>defn get-peer-info
  [client]
  [::s.client/config => any?]
  (handle-request client :getpeerinfo))
