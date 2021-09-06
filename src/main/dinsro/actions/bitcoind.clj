(ns dinsro.actions.bitcoind
  (:require
   [taoensso.timbre :as log])
  (:import
   java.net.URL
   org.bitcoinj.params.MainNetParams
   org.bitcoinj.params.RegTestParams
   org.bitcoinj.wallet.KeyChainGroup
   wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient))

(defn get-params
  []
  (RegTestParams/get))

(defn get-mainnet-params
  []
  (MainNetParams/get))

(defn get-keychain-group
  []
  (KeyChainGroup/createBasic (get-mainnet-params)))

(defn get-genesis-block
  []
  (let [block        (.getGenesisBlock (get-mainnet-params))
        transactions (.getTransactions block)
        inputs       (.getInputs (first transactions))]
    (String. (.getScriptBytes (first inputs)))))

(defn get-client
  [url]
  (BitcoinJSONRPCClient. url))

(defn get-url
  []
  (let [user     "rpcuser"
        password "rpcpassword"
        host     "bitcoind.bitcoin"
        port     "18443"]
    (URL. (str "http://" user ":" password "@" host ":" port))))

(defn generate-coins!
  [^BitcoinJSONRPCClient client address]
  (dotimes [_ 100] (.generateToAddress client 1 address)))

(comment
  (def client (get-client (get-url)))
  (def address "")

  (.getWalletInfo client)
  (.listTransactions client)
  (.generateToAddress client 1 address))
