(ns dinsro.actions.ln.nodes
  (:refer-clojure :exclude [next])
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clj-commons.byte-streams :as bs]
   [clojure.core.async :as async]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [xtdb.api :as xt]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   clojure.core.async.impl.channels.ManyToManyChannel
   io.grpc.stub.StreamObserver
   java.io.File
   java.io.FileNotFoundException
   java.net.UnknownHostException
   java.net.URI
   org.bitcoins.lnd.rpc.config.LndInstance
   org.bitcoins.lnd.rpc.LndRpcClient
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.invoices.AsynchronousInvoicesAPI
   org.lightningj.lnd.wrapper.message.AddressType
   org.lightningj.lnd.wrapper.walletunlocker.AsynchronousWalletUnlockerAPI
   org.lightningj.lnd.wrapper.walletunlocker.SynchronousWalletUnlockerAPI
   scala.Option))

(def default-passphrase "password12345678")

(>defn get-cert-text
  [node]
  [::m.ln.nodes/item => (? string?)]
  (try
    (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id node)))
    (catch FileNotFoundException _ nil)))

(>defn get-macaroon-hex
  [node]
  [::m.ln.nodes/item => (? string?)]
  (try
    (let [f (m.ln.nodes/macaroon-file (::m.ln.nodes/id node))]
      (bytes->hex (bs/to-byte-array f)))
    (catch FileNotFoundException _ nil)))

(>defn get-client
  "Get a lightningj client"
  [{::m.ln.nodes/keys [id name host port]}]
  [::m.ln.nodes/item => (ds/instance? AsynchronousLndAPI)]
  (log/info :client/creating {:id id :name name})
  (let [port-num (Integer/parseInt port)
        cert-file (m.ln.nodes/cert-file id)
        macaroon-file (io/file (m.ln.nodes/macaroon-path id))]
    (c.lnd/get-client host port-num cert-file macaroon-file)))

(defn get-client-s
  "Get a bitcoin-s client"
  ^LndRpcClient [{::m.ln.nodes/keys [id name host port] :as node}]
  (log/info :get-client-s/creating {:id id :name name})
  (let [url       (URI. (str "https://" host ":" port "/"))
        cert-file (Option/apply (get-cert-text node))
        macaroon  (or (get-macaroon-hex node) "")]
    (if macaroon
      (let [instance (c.lnd-s/get-remote-instance url macaroon (Option/empty) cert-file)]
        (log/finer :get-client-s/creating {:url url :cert-file cert-file :macaroon macaroon})
        (c.lnd-s/get-remote-client instance))
      (throw (RuntimeException. "No macaroon")))))

(>defn get-invoices-client
  "Get a lightningj invoices client"
  [node]
  [::m.ln.nodes/item => (ds/instance? AsynchronousInvoicesAPI)]
  (let [{::m.ln.nodes/keys [host id port]} node]
    (c.lnd/get-invoices-client
     host (Integer/parseInt port)
     (io/file (m.ln.nodes/cert-path id))
     (io/file (m.ln.nodes/macaroon-path id)))))

(>defn get-unlocker-client
  "Get a lightningj unlocker client"
  [node]
  [::m.ln.nodes/item => (ds/instance? AsynchronousWalletUnlockerAPI)]
  (let [{::m.ln.nodes/keys [host id port]} node
        port-num                           (Integer/parseInt port)
        file                               (io/file (m.ln.nodes/cert-path id))]
    (log/info :get-unlocker-client/starting {:host host :port-num port-num :id id :file file})
    (c.lnd/get-unlocker-client host port-num file)))

(>defn get-sync-unlocker-client
  "Get a lightningj unlocker client"
  ^SynchronousWalletUnlockerAPI [node]
  [::m.ln.nodes/item => (ds/instance? SynchronousWalletUnlockerAPI)]
  (let [{::m.ln.nodes/keys [host id port]} node]
    (SynchronousWalletUnlockerAPI.
     host
     (Integer/parseInt port)
     (io/file (m.ln.nodes/cert-path id))
     nil)))

(>defn download-file
  "Download a file from a remote uri"
  [uri file]
  [string? (partial instance? File) => boolean?]
  (try
    (with-open [in  (io/input-stream uri)
                out (io/output-stream file)]
      (io/copy in out))
    true
    (catch UnknownHostException _ex
      (log/warn :download/unknown-host {:uri uri :file file})
      false)))

(>defn has-cert?
  "Is the cert available for this node?"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :cert/checking {:node-id id})
  (m.ln.nodes/has-cert? id))

(>defn has-macaroon?
  "Is the macaroon available for this node?"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :macaroon/checking {:node-id id})
  (m.ln.nodes/has-macaroon? id))

(>defn delete-cert
  "Delete the cert file"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/cert-path id)
        f    (io/file path)]
    (.delete f)))

(>defn delete-macaroon
  "Delete the macaroon file"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/macaroon-path id)
        f    (io/file path)]
    (.delete f)))

(>defn download-cert!
  "Download the cert from the fileserver"
  [node]
  [::m.ln.nodes/item => boolean?]
  (let [{::m.ln.nodes/keys [host id]} node
        dir-path                      (m.ln.nodes/data-path id)
        dir-file                      (io/file dir-path)
        url                           (format "http://%s/tls.cert" host)]
    (log/info
     :download-cert!/downloading
     {:id       id
      :dir-path dir-path
      :dir-file dir-file
      :url      url})
    (.mkdirs dir-file)
    (let [cert-file (m.ln.nodes/cert-file id)]
      (download-file url cert-file))))

(>defn download-macaroon!
  "Download the macaroon from the fileserver"
  [{::m.ln.nodes/keys [id host]}]
  [::m.ln.nodes/item => (? (ds/instance? File))]
  (let [url      (format "http://%s/admin.macaroon" host)
        dir-path (m.ln.nodes/data-path id)
        file     (io/file (m.ln.nodes/macaroon-path id))]
    (log/info
     :download-macaroon!/starting
     {:id id :file file :url url :dir-path dir-path})
    (.mkdirs (io/file dir-path))
    (try
      (if (download-file url file)
        file
        (do
          (log/error :download-macaroon!/failed {:node-id id :host host})
          nil))
      (catch FileNotFoundException _ex
        (log/error :download-macaroon!/download-failed {:url url})
        nil))))

(defn get-macaroon-text
  [node]
  (slurp (download-macaroon! node)))

(defn balance-observer
  [next]
  (reify StreamObserver
    (onNext [_this note] (next note))
    (onError [_this err] (println err))
    (onCompleted [_this] (println "onCompleted server"))))

(defn next-address
  [client]
  (let [ch      (async/chan)
        request (c.lnd/->addr-request "")]
    (.nextAddr client request (c.lnd/ch-observer ch))
    ch))

(>defn get-lnd-address
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (c.lnd/->new-address-request)]
      (.newAddress client request (c.lnd/ch-observer ch))
      ch)))

(>defn fetch-address!
  [node-id]
  [::m.ln.nodes/id => (ds/instance? ManyToManyChannel)]
  (let [node (q.ln.nodes/read-record node-id)]
    (get-lnd-address node)))

(>defn generate!
  [node]
  [::m.ln.nodes/item => any?]
  (log/info :node/generating-blocks {:node-id (::m.ln.nodes/id node)})
  (let [{:keys [address]} (async/<!! (get-lnd-address node))
        cnode             (first (q.c.nodes/index-records))]
    (a.c.nodes/generate-to-address! cnode address)
    address))

(>defn initialize!
  [{::m.ln.nodes/keys [mnemonic] :as node}]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-unlocker-client node)]
    (let [request (c.lnd/->init-wallet-request mnemonic "password12345678")
          ch      (async/chan)]
      (log/info :initialize!/starting {:request request :client client})
      (.initWallet client request (c.lnd/ch-observer ch))
      ch)))

(>defn initialize!-sync
  [{::m.ln.nodes/keys [mnemonic] :as node}]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-sync-unlocker-client node)]
    (let [request (c.lnd/->init-wallet-request mnemonic  "password12345678")]
      (log/info :initialize-sync!/starting {:request request})
      (.initWallet client request))))

(defn initialize!-s
  [node]
  (log/info :initialize!-s/starting {:node node})
  (let [client (get-client-s node)]
    (c.lnd-s/initialize! client "password12345678")))

(>defn save-info!
  [id data]
  [::m.ln.nodes/id ::m.ln.info/params => any?]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        entity (xt/entity db id)
        tx     (xt/submit-tx node [[::xt/put (merge entity data)]])]
    (xt/await-tx node tx)))

(>defn update-info!
  [{::m.ln.nodes/keys [id] :as node}]
  [::m.ln.nodes/item => any?]
  (log/info :update-info!/starting {:id id})
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getInfo client (c.lnd/ch-observer ch))
      (async/go
        (let [response (async/<! ch)
              params   (set/rename-keys response m.ln.info/rename-map)]
          (log/info
           :update-info!/saving
           {:id       id
            :response response
            :params   params})
          (save-info! id params)))
      ch)))

(>defn unlock-sync!
  [node]
  [::m.ln.nodes/item => any?]
  (log/info :unlock-sync!/starting {:node-id (::m.ln.nodes/id node)})
  (let [wallet-passphrase default-passphrase]
    (with-open [client (get-sync-unlocker-client node)]
      (let [request (c.lnd/->unlock-wallet-request wallet-passphrase)]
        (.unlockWallet client request)))))

(>defn new-address
  [node f]
  [::m.ln.nodes/item any? => any?]
  (with-open [client (get-client node)]
    (.newAddress client AddressType/WITNESS_PUBKEY_HASH "" (balance-observer f))))

(defn get-remote-instance
  "Create a Bitcoin-s lnd remote instance for node"
  ^LndInstance [{::m.ln.nodes/keys [host port] :as node}]
  (let [url       (URI. (str "http://" host ":" port "/"))
        macaroon  (get-macaroon-text node)]
    (c.lnd-s/get-remote-instance url macaroon)))

(defn get-info
  "Fetch info for the node"
  [node]
  (let [client (get-client-s node)
        response (c.lnd-s/get-info client)]
    (log/info :get-info/response {:response response})
    (let [record (cs/->record response)]
      (log/info :get-info/converted {:record record})
      record)))

(>defn new-address-s
  [node]
  [::m.ln.nodes/item => any?]
  (let [client (get-client-s node)]
    (c.lnd-s/get-new-address client)))

(defn new-address-str
  [node]
  (.value (new-address-s node)))

(defn unlock-sync!-s
  [node]
  (log/info :unlock-sync!-s/starting {:node-id (::m.ln.nodes/id node)})
  (let [client     (get-client-s node)
        passphrase default-passphrase]
    (c.lnd-s/unlock-wallet client passphrase)))
