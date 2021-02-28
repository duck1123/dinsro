(ns dinsro.actions.lnd
  (:require
   [clojure.data.json :as json]
   [puget.printer :as puget]
   [taoensso.timbre :as timbre])
  (:import
   java.io.File
   org.lightningj.lnd.wrapper.message.ListChannelsRequest
   org.lightningj.lnd.wrapper.message.ListInvoiceRequest
   org.lightningj.lnd.wrapper.SynchronousLndAPI))

(defn get-api
  [config]
  (SynchronousLndAPI. (:host config)
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

(defn list-peers
  [s-lnd-api]
  (parse (.listPeers s-lnd-api)))

(defn channel-balance
  [s-lnd-api]
  (parse (.channelBalance s-lnd-api)))

(defn index-invoices
  [api]
  (let [request (ListInvoiceRequest.)]
    (parse (.listInvoices api request))))

(comment
  (def wallet-base "/usr/src/app/")

  (def config {:host "192.168.0.28"
               :port 10009
               :cert-path (str wallet-base "tls.cert")
               :macaroon-path (str wallet-base "admin.macaroon")})

  (def s-lnd-api (get-api config))

  (parse (.getInfo s-lnd-api))

  (list-peers s-lnd-api)

  (index-invoices s-lnd-api)

  (first (:channels (list-channels s-lnd-api)))

  (channel-balance s-lnd-api)
  (spit "backups.txt"  (.exportAllChannelBackups s-lnd-api))
  (puget/cprint (parse (.listPayments s-lnd-api false))))
