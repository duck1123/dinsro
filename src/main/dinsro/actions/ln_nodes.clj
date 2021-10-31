(ns dinsro.actions.ln-nodes
  (:refer-clojure :exclude [next])
  (:require
   [buddy.core.codecs :as bcc]
   [clojure.core.async :as async :refer [>!! <! <!!]]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [crux.api :as crux]
   [dinsro.actions.bitcoind :as a.bitcoind]
   [dinsro.actions.lnd :as a.lnd]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.ln-channels :as m.ln-channels]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.ln-channels :as q.ln-channels]
   [dinsro.queries.ln-nodes :as q.ln-nodes]
   [dinsro.queries.ln-peers :as q.ln-peers]
   [dinsro.queries.ln-transactions :as q.ln-tx]
   [dinsro.specs :as ds]
   [taoensso.timbre :as log])
  (:import
   clojure.core.async.impl.channels.ManyToManyChannel
   io.grpc.stub.StreamObserver
   java.io.File
   java.io.FileNotFoundException
   org.lightningj.lnd.wrapper.Message
   org.lightningj.lnd.wrapper.message.AddressType
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.walletunlocker.message.InitWalletRequest
   org.lightningj.lnd.wrapper.message.ConnectPeerRequest
   org.lightningj.lnd.wrapper.message.GetTransactionsRequest
   org.lightningj.lnd.wrapper.message.LightningAddress
   org.lightningj.lnd.wrapper.message.ListChannelsRequest
   org.lightningj.lnd.wrapper.message.ListInvoiceRequest
   org.lightningj.lnd.wrapper.message.ListPaymentsRequest
   org.lightningj.lnd.wrapper.message.NewAddressRequest
   org.lightningj.lnd.wrapper.walletkit.message.AddrRequest
   org.lightningj.lnd.wrapper.walletunlocker.message.UnlockWalletRequest
   org.lightningj.lnd.wrapper.walletunlocker.AsynchronousWalletUnlockerAPI))

(>defn get-client
  [{::m.ln-nodes/keys [id name host port]}]
  [::m.ln-nodes/item => (ds/instance? AsynchronousLndAPI)]
  (log/infof "Getting Client - %s" name)
  (AsynchronousLndAPI.
   host
   (Integer/parseInt port)
   (m.ln-nodes/cert-file id)
   (io/file (m.ln-nodes/macaroon-path id))))

(>defn get-unlocker-client
  [node]
  [::m.ln-nodes/item => (ds/instance? AsynchronousWalletUnlockerAPI)]
  (let [{::m.ln-nodes/keys [host id port]} node]
    (AsynchronousWalletUnlockerAPI.
     host
     (Integer/parseInt port)
     (io/file (m.ln-nodes/cert-path id))
     nil)))

(>defn download-file
  [uri file]
  [string? (partial instance? File) => nil?]
  (with-open [in  (io/input-stream uri)
              out (io/output-stream file)]
    (io/copy in out)))

(>defn download-cert!
  [node]
  [::m.ln-nodes/item => nil?]
  (let [{::m.ln-nodes/keys [host id]} node]
    (log/infof "Downloading2 cert for %s" id)
    (log/spy :info (.mkdirs (log/spy :info (io/file (str m.ln-nodes/cert-base id)))))
    (let [url       (format "http://%s/tls.cert" host)
          cert-file (m.ln-nodes/cert-file id)]
      (download-file url cert-file))))

(>defn download-macaroon!
  [{::m.ln-nodes/keys [id host]}]
  [::m.ln-nodes/item => (? (ds/instance? File))]
  (let [url  (format "http://%s/admin.macaroon" host)
        file (io/file (m.ln-nodes/macaroon-path id))]
    (log/infof "Downloading macaroon for %s" id)
    (.mkdirs (log/spy :info (io/file (str m.ln-nodes/cert-base id))))
    (try
      (download-file url file)
      file
      (catch FileNotFoundException ex
        (log/error ex "Failed to download")
        nil))))

(defn get-init-wallet-request
  [mnemonic]
  (let [request (InitWalletRequest.)]
    (.setCipherSeedMnemonic request mnemonic)
    (.setWalletPassword request (bcc/str->bytes "password12345678"))
    request))

(defn balance-observer
  [next]
  (reify StreamObserver
    (onNext [this note] (next note))
    (onError [this err] (println err))
    (onCompleted [this] (println "onCompleted server"))))

(defn ch-observer
  [ch]
  (reify StreamObserver
    (onNext [this message]
      (let [data (a.lnd/parse message)]
        (log/infof "next: %s" data)
        (>!! ch data)))
    (onError [this err]
      (log/infof "error: %s" err)
      (>!! ch {:error err})
      (async/close! ch))
    (onCompleted [this]
      (log/info "close")
      (async/close! ch))))

(defn ->list-channels-request
  []
  (let [request (ListChannelsRequest.)]
    (.setActiveOnly request true)
    request))

(defn ->get-transactions-request
  []
  (let [request (GetTransactionsRequest.)]
    request))

(>defn fetch-channels
  [node]
  [::m.ln-nodes/item => (ds/instance? ManyToManyChannel)]
  (let [ch      (async/chan)
        request (->list-channels-request)]
    (with-open [client (get-client node)]
      (.listChannels client request (ch-observer ch)))
    ch))

(>defn fetch-transactions
  [node]
  [::m.ln-nodes/item => (ds/instance? ManyToManyChannel)]
  (let [ch      (async/chan)
        request (->get-transactions-request)]
    (with-open [client (get-client node)]
      (.getTransactions client request (ch-observer ch)))
    ch))

(>defn fetch-peers
  [node]
  [::m.ln-nodes/item => (ds/instance? ManyToManyChannel)]
  (let [ch (async/chan)]
    (with-open [client (get-client node)]
      (.listPeers client true (ch-observer ch)))
    ch))

(defn create-peer-record!
  [data]
  (log/infof "create peer record: %s" data)
  (q.ln-peers/create-record data))

(>defn ->addr-request
  [account]
  [string? => (ds/instance? AddrRequest)]
  (let [request (AddrRequest.)]
    (.setAccount request account)
    request))

(defn next-address
  [client]
  (let [ch      (async/chan)
        request (->addr-request "")]
    (.nextAddr client request (ch-observer ch))
    ch))

(defn ->new-address-request
  []
  (let [request (NewAddressRequest.)]
    request))

(defn get-lnd-address
  [node]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (->new-address-request)]
      (.newAddress client request (ch-observer ch))
      ch)))

(>defn fetch-address!
  [node-id]
  [::m.ln-nodes/id => (ds/instance? ManyToManyChannel)]
  (let [node (q.ln-nodes/read-record node-id)]
    (get-lnd-address node))
  #_(let [ch (async/chan)]
      (with-open [client (get-client node)]
        (.listPeers client true (ch-observer ch)))
      ch))

(>defn generate!
  [node]
  [::m.ln-nodes/item => any?]
  (log/info "Generating to node")
  (let [{:keys [address]} (async/<!! (get-lnd-address node))
        cnode             (first (q.core-nodes/index-records))]
    (a.bitcoind/generate-to-address! cnode (log/spy :info address))
    address))

(>defn update-channel!
  [node data]
  [::m.ln-nodes/item ::m.ln-peers/params => any?]
  (let [{::m.ln-nodes/keys [id]}               node
        {::m.ln-channels/keys [channel-point]} data]
    (if-let [peer (q.ln-channels/find-channel id channel-point)]
      (do
        (log/infof "has channel: %s" peer)
        nil)
      (do
        (log/error "no channel")
        (let [{:keys []} data]
          (create-peer-record! (assoc data ::m.ln-peers/node id)))))))

(>defn update-peer!
  [node data]
  [::m.ln-nodes/item ::m.ln-peers/params => any?]
  (let [{::m.ln-nodes/keys [id]}     node
        {::m.ln-peers/keys [pubkey]} data]
    (if-let [peer (q.ln-peers/find-peer id pubkey)]
      (do
        (log/infof "has peer: %s" peer)
        nil)
      (do
        (log/error "no peer")
        (let [{:keys []} data]
          (create-peer-record! (assoc data ::m.ln-peers/node id)))))))

(>defn update-transaction!
  [node data]
  [::m.ln-nodes/item ::m.ln-tx/raw-params => any?]
  (let [{::m.ln-nodes/keys [id]}  node
        {::m.ln-tx/keys [tx-hash]} (log/spy :info data)]
    (if-let [tx-id (q.ln-tx/find-id-by-node-and-tx-hash id tx-hash)]
      (do
        (log/infof "has tx: %s" tx-id)
        nil)
      (do
        (log/error "no tx")
        (let [tx (assoc data ::m.ln-tx/node id)]
          (q.ln-tx/create-record (log/spy :info tx)))))))

(>defn fetch-channels!
  [id]
  [uuid? => (? any?)]
  (log/infof "Fetching Channels - %s" id)
  (if-let [node (q.ln-nodes/read-record id)]
    (if-let [ch (fetch-channels node)]
      (do
        (async/go
          (let [data            (async/<! ch)
                {:keys [peers]} data]
            (doseq [peer peers]
              (try
                (update-channel! node (set/rename-keys peer m.ln-channels/rename-map))
                (catch Exception ex
                  (log/error "Failed to update" ex))))))
        ch)
      (do
        (log/error "channel error")
        nil))
    (do
      (log/error "No Node")
      nil)))

(>defn fetch-transactions!
  [node-id]
  [::m.ln-nodes/id => (? any?)]
  (log/infof "Fetching Transactions - %s" node-id)
  (if-let [node (q.ln-nodes/read-record node-id)]
    (if-let [ch (fetch-transactions node)]
      (do
        (async/go
          (let [data            (async/<! ch)
                {:keys [transactions]} data]
            (doseq [transaction transactions]
              (let [params (set/rename-keys transaction m.ln-tx/rename-map)]
                (try
                  (update-transaction! node params)
                  (catch Exception ex
                    (log/error "Failed to update" ex)))))))
        ch)
      (do
        (log/error "channel error")
        nil))
    (do
      (log/error "No Node")
      nil)))

(>defn fetch-peers!
  [id]
  [uuid? => (? any?)]
  (log/infof "Fetching Peers - %s" id)
  (if-let [node (q.ln-nodes/read-record id)]
    (if-let [ch (fetch-peers node)]
      (do
        (async/go
          (let [data            (async/<! ch)
                {:keys [peers]} data]
            (doseq [peer peers]
              (try
                (update-peer! node (set/rename-keys peer m.ln-peers/rename-map))
                (catch Exception ex
                  (log/error "Failed to update" ex))))))
        ch)
      (do
        (log/error "peer error")
        nil))
    (do
      (log/error "No Node")
      nil)))

(defn initialize!
  [{::m.ln-nodes/keys [mnemonic] :as node}]
  (with-open [client (get-unlocker-client (log/spy :info node))]
    (let [request (get-init-wallet-request (log/spy :info mnemonic))
          ch      (async/chan)]
      (log/info "Initializing Wallet")
      (.initWallet client request (ch-observer ch))
      ch)))

(>defn save-info!
  [id data]
  [::m.ln-nodes/id ::m.ln-info/params => any?]
  (let [node   (c.crux/main-node)
        db     (c.crux/main-db)
        entity (crux/entity db id)
        tx     (crux/submit-tx node [[:crux.tx/put (merge entity data)]])]
    (crux/await-tx node tx)))

(defn update-info!
  [{::m.ln-nodes/keys [id] :as node}]
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getInfo client (ch-observer ch))
      (async/go (save-info! id (set/rename-keys (async/<! ch) m.ln-info/rename-map)))
      ch)))

(defn save-transactions!
  [id note]
  (let [data   (a.lnd/parse note)
        params (set/rename-keys data m.ln-info/rename-map)
        node   (c.crux/main-node)
        db     (c.crux/main-db)
        entity (crux/entity db id)
        tx     (crux/submit-tx node [[:crux.tx/put (merge entity params)]])]
    (crux/await-tx node tx)))

(defn update-transactions!
  [{::m.ln-nodes/keys [id] :as node}]
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getInfo client (ch-observer ch))
      (async/go (save-transactions! id (async/<! ch)))
      ch)))

(defn unlocker-request
  [_node]
  (let [request (UnlockWalletRequest.)]
    (.setWalletPassword request (bcc/str->bytes "password12345678"))
    request))

(defn unlock!
  [node f]
  (with-open [client (get-unlocker-client node)]
    (let [request (unlocker-request node)]
      (.unlockWallet client request (balance-observer f)))))

(defn new-address
  [node f]
  (with-open [client (get-client node)]
    (.newAddress client AddressType/WITNESS_PUBKEY_HASH "" (balance-observer f))))

(defn parse
  [^Message m]
  (json/read-str (.toJsonAsString m false) :key-fn keyword))

(>defn ->lightning-address
  [host pubkey]
  [string? string? => (partial instance? LightningAddress)]
  (let [address (LightningAddress.)]
    (doto address
      (.setHost host)
      (.setPubkey pubkey))))

(>defn node->lightning-address
  [node]
  [::m.ln-nodes/item => (partial instance? LightningAddress)]
  (let [{::m.ln-nodes/keys [host pubkey]} node]
    (->lightning-address host pubkey)))

(>defn ->connect-peer-request
  [host pubkey]
  [string? string? => (partial instance? ConnectPeerRequest)]
  (let [request (ConnectPeerRequest.)
        address (->lightning-address host pubkey)]
    (doto request
      (.setAddr address))
    request))

(>defn ->list-invoice-request
  []
  [=> (partial instance? ListInvoiceRequest)]
  (let [request (ListInvoiceRequest.)]
    request))

(defn list-payment-request
  []
  (let [request (ListPaymentsRequest.)]
    request))

(comment
  (->lightning-address "lnd.localhost" "deadbeef")
  (->connect-peer-request "lnd.localhost" "deadbeef")

  nil)

(defn get-transactions
  [node]
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getTransactions client (int 0) (int 0) "" (ch-observer ch))
      ch)))

(defn update-transactions
  [{::m.ln-nodes/keys [id] :as node}]
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getTransactions client (int 0) (int 0) "" (ch-observer ch))
      (async/go
        (let [response               (<! ch)
              {:keys [transactions]} (parse response)]
          (mapv
           (fn [transaction]
             (let [params (set/rename-keys transaction m.ln-tx/rename-map)]
               (q.ln-tx/create-record (assoc params ::m.ln-tx/node-id id))))
           transactions))))))

(>defn create-peer!
  [node host pubkey]
  [::m.ln-nodes/item string? string? => any?]
  (log/infof "Creating peer: %s@%s" pubkey host)
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (->connect-peer-request host pubkey)]
      (.connectPeer client request (ch-observer ch))
      ch)))

(defn fetch-invoices
  [node]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (->list-invoice-request)]
      (.listInvoices client request (ch-observer ch))
      ch)))

(defn fetch-payments
  [node]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (list-payment-request)]
      (.listPayments client request (ch-observer ch))
      ch)))

(def payment-map {})

(defn update-payments
  [node]
  (async/map
   (fn [response]
     (let [{:keys [payments]} response]
       (map (fn [payment] (set/rename-keys payment payment-map)) payments)))
   (fetch-payments node)))

(comment
  (download-cert! (first (q.ln-nodes/index-ids)))

  (def node (merge (ds/gen-key ::m.ln-nodes/item)
                   {::m.ln-nodes/cert-path     "tls.cert"
                    ::m.ln-nodes/macaroon-path "admin.macaroon"}))

  (def node1 (first (q.ln-nodes/index-records)))
  (def node2 (second (q.ln-nodes/index-records)))

  node1
  node2

  (generate! node1)

  node

  (update-payments node)

  (update-info! node)
  (get-transactions node)

  (fetch-peers node)

  (fetch-invoices node)
  (fetch-payments node)

  (q.ln-peers/index-records)

  (let [ch (async/chan 1)]
    (async/pipeline
     1
     ch
     (map (fn [z] z))
     (fetch-payments node)
     true
     (fn [error] (println "ahhh: " (.getMessage error)))))

  (<!! (fetch-payments node1))

  (let [chan (fetch-payments node1)]
    (async/go-loop []
      (let [a (<! chan)] (when a a (recur)))))

  (<!! (initialize! node1))

  (new-address node (fn [response] response))

  (unlock! node (fn [response] response))

  (update-transactions node)

  (get-client node))
