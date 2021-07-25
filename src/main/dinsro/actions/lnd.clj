(ns dinsro.actions.lnd
  (:require
   [clojure.data.json :as json]
   [puget.printer :as puget]
   [taoensso.timbre :as log])
  (:import
   io.grpc.stub.StreamObserver
   java.io.File
   org.bitcoinj.params.MainNetParams
   org.bitcoinj.params.RegTestParams
   org.bitcoinj.wallet.KeyChainGroup
   org.lightningj.lnd.wrapper.message.ListChannelsRequest
   org.lightningj.lnd.wrapper.message.ListInvoiceRequest
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.SynchronousLndAPI))

(defn get-api
  [config]
  (SynchronousLndAPI. (:host config)
                      (:port config)
                      (File. (:cert-path config))
                      (File. (:macaroon-path config))))

(defn get-params
  []
  (RegTestParams/get))

(defn get-mainnet-params
  []
  (MainNetParams/get))

(defn get-keychain-group
  []
  (KeyChainGroup/createBasic (get-mainnet-params)))

(defn get-async-api
  [config]
  (AsynchronousLndAPI.
   (:host config)
   (:port config)
   (File. (:cert-path config))
   (File. (:macaroon-path config))))

(defn parse
  [m]
  (json/read-str (.toJsonAsString m false) :key-fn keyword))

(defn list-channels
  [api]
  (let [request (ListChannelsRequest.)]
    (.setActiveOnly request true)
    (parse (.listChannels api request))))

(defn balance-observer
  []
  (reify StreamObserver
    (onNext [this note]
      ;; there will be multiple .onNext here
      (println (parse note)))
    (onError [this err]
      (println err))
    (onCompleted [this]
      (println "onCompleted server")
      ;; there will be no .onNext here
      #_(.onCompleted res))))

(defn list-peers
  [s-lnd-api]
  (parse (.listPeers s-lnd-api)))

(defn channel-balance
  [s-lnd-api]
  (parse (.channelBalance s-lnd-api)))

(defn channel-balance-a
  [a-lnd-api]
  (.channelBalance a-lnd-api
                   (balance-observer)))

(defn index-invoices
  [api]
  (let [request (ListInvoiceRequest.)]
    (parse (.listInvoices api request))))

(def wallet-base "/usr/src/app/")

(def lnd-host "127.0.0.1")
(def payment-request "")

(def config {:host          lnd-host
             :port          10009
             :cert-path     (str wallet-base "tls.cert")
             :macaroon-path (str wallet-base "admin.macaroon")})

(comment

  (def s-lnd-api (get-api config))
  (def a-lnd-api (get-async-api config))

  (parse (.getInfo s-lnd-api))

  (String.
   (.getScriptBytes
    (first
     (.getInputs
      (first
       (.getTransactions
        (.getGenesisBlock
         (get-mainnet-params))))))))

  (list-peers s-lnd-api)

  (index-invoices s-lnd-api)

  (first (:channels (list-channels s-lnd-api)))

  (channel-balance-a a-lnd-api)

  (.getTransactions s-lnd-api
                    ;; 0 100
                    (int 0) (int 100) #_(balance-observer))

  (.listPayments a-lnd-api
                 true
                 (long 0)
                 (long 100)
                 false
                 (balance-observer))

  (.listPeers a-lnd-api (balance-observer))

  (list-peers s-lnd-api)

  (puget/cprint (parse (.listPayments s-lnd-api false)))

  (.getNumSatoshis (.decodePayReq s-lnd-api payment-request))

  (channel-balance s-lnd-api)

  (channel-balance s-lnd-api)
  (spit "backups.txt"  (.exportAllChannelBackups s-lnd-api))
  (puget/cprint (parse (.listPayments s-lnd-api false))))
