(ns dinsro.actions.lnd
  (:require
   [clojure.data.json :as json]
   [puget.printer :as puget]
   [taoensso.timbre :as timbre])
  (:import
   java.io.File
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

(defn list-peers
  [s-lnd-api]
  (parse (.listPeers s-lnd-api)))

(defn channel-balance
  [s-lnd-api]
  (parse (.channelBalance s-lnd-api)))

(comment
  (let [wallet-base "/home/duck/.config/Zap/lnd/bitcoin/mainnet/wallet-2/"
        config {:host "localhost"
                :port 11009
                :cert-path (str wallet-base "tls.cert")
                :macaroon-path (str wallet-base "data/chain/bitcoin/mainnet/admin.macaroon")}
        s-lnd-api (get-api config)]
    (list-peers s-lnd-api)
    (channel-balance s-lnd-api)
    (spit "backups.txt"  (.exportAllChannelBackups s-lnd-api))
    (puget/cprint (parse (.listPayments s-lnd-api false)))))
