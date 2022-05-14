(ns dinsro.actions.ln.nodes
  (:refer-clojure :exclude [next])
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clj-commons.byte-streams :as bs]
   [clojure.core.async :as async :refer [<!!]]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [xtdb.api :as xt]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.users :as q.users]
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

(>defn get-cert-text
  [node]
  [::m.ln.nodes/item => string?]
  (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id node))))

(>defn get-macaroon-hex
  [node]
  [::m.ln.nodes/item => string?]
  (let [f (m.ln.nodes/macaroon-file (::m.ln.nodes/id node))]
    (bytes->hex (bs/to-byte-array f))))

(>defn get-client
  [{::m.ln.nodes/keys [id name host port]}]
  [::m.ln.nodes/item => (ds/instance? AsynchronousLndAPI)]
  (log/info :client/creating {:id id :name name})
  (c.lnd/get-client
   host
   (Integer/parseInt port)
   (m.ln.nodes/cert-file id)
   (io/file (m.ln.nodes/macaroon-path id))))

(defn get-client-s
  "Get a bitcoin-s client"
  ^LndRpcClient [{::m.ln.nodes/keys [id name host port] :as node}]
  (log/info :client/creating {:id id :name name})
  (let [url       (URI. (str "https://" host ":" port "/"))
        cert-file (Option/apply (get-cert-text node))
        macaroon  (get-macaroon-hex node)
        instance  (c.lnd-s/get-remote-instance url macaroon (Option/empty) cert-file)]
    (log/info :get-client-s/creating {:url url :cert-file cert-file :macaroon macaroon})
    (c.lnd-s/get-remote-client instance)))

(>defn get-invoices-client
  [node]
  [::m.ln.nodes/item => (ds/instance? AsynchronousInvoicesAPI)]
  (let [{::m.ln.nodes/keys [host id port]} node]
    (c.lnd/get-invoices-client
     host (Integer/parseInt port)
     (io/file (m.ln.nodes/cert-path id))
     (io/file (m.ln.nodes/macaroon-path id)))))

(>defn get-unlocker-client
  [node]
  [::m.ln.nodes/item => (ds/instance? AsynchronousWalletUnlockerAPI)]
  (let [{::m.ln.nodes/keys [host id port]} node]
    (c.lnd/get-unlocker-client
     host (Integer/parseInt port)
     (io/file (m.ln.nodes/cert-path id)))))

(>defn get-sync-unlocker-client
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
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :cert/checking {:node-id id})
  (m.ln.nodes/has-cert? id))

(>defn has-macaroon?
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :macaroon/checking {:node-id id})
  (m.ln.nodes/has-macaroon? id))

(>defn delete-cert
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/cert-path id)
        f    (io/file path)]
    (.delete f)))

(>defn delete-macaroon
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/macaroon-path id)
        f    (io/file path)]
    (.delete f)))

(>defn download-cert!
  [node]
  [::m.ln.nodes/item => boolean?]
  (let [{::m.ln.nodes/keys [host id]} node]
    (log/info :cert/downloading {:id id})
    (.mkdirs (io/file (str m.ln.nodes/cert-base id)))
    (let [url       (format "http://%s/tls.cert" host)
          cert-file (m.ln.nodes/cert-file id)]
      (download-file url cert-file))))

(>defn download-macaroon!
  [{::m.ln.nodes/keys [id host]}]
  [::m.ln.nodes/item => (? (ds/instance? File))]
  (let [url  (format "http://%s/admin.macaroon" host)
        file (io/file (m.ln.nodes/macaroon-path id))]
    (log/info :macaroon/downloading {:id id})
    (.mkdirs (io/file (str m.ln.nodes/cert-base id)))
    (try
      (if (download-file url file)
        file
        (do
          (log/error :macaroon-download/failed {:node-id id :host host})
          nil))
      (catch FileNotFoundException ex
        (log/error :macaroon/download-failed {:exception ex})
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
      (log/info :wallet/initializing {})
      (.initWallet client request (c.lnd/ch-observer ch))
      ch)))

(>defn initialize!-sync
  [{::m.ln.nodes/keys [mnemonic] :as node}]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-sync-unlocker-client node)]
    (let [request (c.lnd/->init-wallet-request mnemonic  "password12345678")]
      (log/info :wallet/initializing-sync {})
      (.initWallet client request))))

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
  (log/info :node/unlocking {:node-id (::m.ln.nodes/id node)})
  (with-open [client (get-sync-unlocker-client node)]
    (let [request (c.lnd/->unlock-wallet-request "password12345678")]
      (.unlockWallet client request))))

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

(comment
  (download-cert! (first (q.ln.nodes/index-ids)))

  (def user-alice (q.users/find-eid-by-name "alice"))
  (def user-bob (q.users/find-eid-by-name "bob"))

  user-alice
  user-bob

  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id node)))

  (def client (get-client node))
  client

  (with-open [client (get-client node)] (c.lnd/list-invoices client))
  (c.lnd/list-payments client)

  (q.ln.nodes/index-ids)

  (generate! node-alice)

  (update-info! node)

  (delete-cert node)
  (has-cert? node)
  (download-cert! node)

  (delete-macaroon node)
  (has-macaroon? node)

  (prn (slurp (download-macaroon! node)))

  (println (get-macaroon-text node))
  (get-remote-instance node)

  (def remote-client (c.lnd-s/get-remote-client (get-remote-instance node)))

  remote-client

  (c.lnd-s/get-info remote-client)

  (def fu (.listPeers remote-client))

  (<!! (initialize! node))

  (q.ln.peers/index-records)

  (new-address node (fn [response] response))

  (get-client node)
  (get-macaroon-hex node)

  (m.ln.nodes/cert-file node)
  (get-cert-text node)
  (def f (m.ln.nodes/macaroon-file (::m.ln.nodes/id node)))

  (bs/convert f)

  (.exists f)

  (def client (get-client-s node))
  client

  (def response (c.lnd-s/get-info client))

  (c.lnd-s/->record response)
  (.alias response)

  (bytes->hex (bs/to-byte-array f))
  nil)
