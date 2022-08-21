(ns dinsro.actions.ln.nodes-lj
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   clojure.core.async.impl.channels.ManyToManyChannel
   io.grpc.stub.StreamObserver
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.invoices.AsynchronousInvoicesAPI
   org.lightningj.lnd.wrapper.message.AddressType
   org.lightningj.lnd.wrapper.walletunlocker.AsynchronousWalletUnlockerAPI
   org.lightningj.lnd.wrapper.walletunlocker.SynchronousWalletUnlockerAPI))

(defn balance-observer
  [next]
  (reify StreamObserver
    (onNext [_this note] (next note))
    (onError [_this err] (println err))
    (onCompleted [_this] (println "onCompleted server"))))

(>defn get-client
  "Get a lightningj client"
  [{::m.ln.nodes/keys [id name host port]}]
  [::m.ln.nodes/item => (ds/instance? AsynchronousLndAPI)]
  (log/finer :get-client/starting {:id id :name name})
  (let [port-num      (Integer/parseInt port)
        cert-file     (m.ln.nodes/cert-file id)
        macaroon-file (io/file (m.ln.nodes/macaroon-path id))]
    (c.lnd/get-client host port-num cert-file macaroon-file)))

(>defn get-lnd-address
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (c.lnd/->new-address-request)]
      (.newAddress client request (c.lnd/ch-observer ch))
      ch)))

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
          (a.ln.nodes/save-info! id params)))
      ch)))

(>defn new-address
  [node f]
  [::m.ln.nodes/item any? => any?]
  (with-open [client (get-client node)]
    (.newAddress client AddressType/WITNESS_PUBKEY_HASH "" (balance-observer f))))

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
    (try
      (let [client (c.lnd/get-unlocker-client host port-num file)]
        (log/info :get-unlocker-client/finished {:client client})
        client)
      (catch Exception ex
        (log/info :get-unlocker-client/errored {:ex ex})
        (throw (RuntimeException. "Failed to get unlocker client" ex))))))

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

(>defn fetch-address!
  [node-id]
  [::m.ln.nodes/id => (ds/instance? ManyToManyChannel)]
  (let [node (q.ln.nodes/read-record node-id)]
    (get-lnd-address node)))

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

(>defn generate!
  [node]
  [::m.ln.nodes/item => any?]
  (log/info :node/generating-blocks {:node-id (::m.ln.nodes/id node)})
  (let [{:keys [address]} (async/<!! (get-lnd-address node))
        cnode             (first (q.c.nodes/index-records))]
    (a.c.nodes/generate-to-address! cnode address)
    address))

(>defn unlock-sync!
  [node]
  [::m.ln.nodes/item => any?]
  (log/info :unlock-sync!/starting {:node-id (::m.ln.nodes/id node)})
  (let [wallet-passphrase a.ln.nodes/default-passphrase]
    (with-open [client (get-sync-unlocker-client node)]
      (let [request (c.lnd/->unlock-wallet-request wallet-passphrase)]
        (.unlockWallet client request)))))
