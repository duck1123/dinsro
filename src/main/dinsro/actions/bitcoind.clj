(ns dinsro.actions.bitcoind
  (:require
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.queries.core-nodes :as q.core-nodes]
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
  ([]
   (let [user     "rpcuser"
         password "rpcpassword"
         host     "bitcoind.bitcoin"
         port     "18443"]
     (URL. (str "http://" user ":" password "@" host ":" port))))
  ([node]
   (let [{::m.core-nodes/keys [rpcuser rpcpass host port]} node]
     (URL. (format "http://%s:%s@%s:%s" rpcuser rpcpass host port)))))

(defn get-client-for-node
  [node]
  (let [url (get-url node)]
    (get-client url)))

(defn generate-coins!
  [^BitcoinJSONRPCClient client address]
  (dotimes [_ 100] (.generateToAddress client 1 address)))

(defn get-blockchain-info
  [node]
  (let [client (get-client-for-node node)
        info   (.getBlockChainInfo client)]
    {:blockchain-info/automatic-pruning      (.automaticPruning info)
     :blockchain-info/best-block-hash        (.bestBlockHash info)
     :blockchain-info/blocks                 (.blocks info)
     :blockchain-info/chain                  (.chain info)
     :blockchain-info/headers                (.headers info)
     :blockchain-info/difficulty             (.difficulty info)
     :blockchain-info/median-time            (.medianTime info)
     :blockchain-info/verification-progress  (.verificationProgress info)
     :blockchain-info/initial-block-download (.initialBlockDownload info)
     :blockchain-info/chain-work             (.chainWork info)
     :blockchain-info/size-on-disk           (.sizeOnDisk info)
     :blockchain-info/pruned                 (.pruned info)
     :blockchain-info/prune-height           (.pruneHeight info)
     :blockchain-info/prune-target-size      (.pruneTargetSize info)
     :blockchain-info/warnings               (.warnings info)}))

(defn get-wallet-info
  [^BitcoinJSONRPCClient client]
  (let [wallet-info (.getWalletInfo client)]
    {:balance  (.balance wallet-info)
     :tx-count (.txCount wallet-info)}))

(defn update-info!
  [{::m.core-nodes/keys [id]}]
  (let [node   (q.core-nodes/read-record id)
        url    (get-url node)
        client (get-client url)
        info   (get-wallet-info client)
        params (assoc info ::m.core-nodes/id id)]
    (q.core-nodes/update-wallet-info params)))

(defn update-blockchain-info!
  [node]
  (let [{::m.core-nodes/keys [id]} node
        info                       (get-blockchain-info node)]
    (q.core-nodes/update-blockchain-info id info)))

(defn generate!
  [node]
  (let [client (get-client-for-node node)]
    (.generate client 10)))

(defn generate-to-address!
  [node address]
  (let [client (get-client-for-node node)]
    (.generateToAddress client 100 address)))

(comment
  (def client (get-client (get-url)))
  (def address "")

  (.getWalletInfo client)
  (.listTransactions client)
  (.generateToAddress client 1 address)

  (def node (first (q.core-nodes/index-records)))

  node

  (update-info! node)
  (generate! node)
  (generate-to-address! node "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

  (get-client-for-node node)
  (get-blockchain-info node)

  (update-blockchain-info! node)

  (q.core-nodes/update-wallet-info (assoc (update-info! node) ::m.core-nodes/id (::m.core-nodes/id node)))

  nil)
