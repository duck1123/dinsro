(ns dinsro.client.lnd-s
  "Clojure interop for Bitcoin-S LND client"
  (:require
   [clojure.core.async :as async]
   [dinsro.client.scala :as cs :refer [Recordable]]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.lnd.rpc.config.LndInstanceRemote
   org.bitcoins.lnd.rpc.config.LndInstance
   org.bitcoins.lnd.rpc.LndRpcClient
   scala.Option
   lnrpc.Chain
   lnrpc.ConnectPeerRequest
   lnrpc.GetInfoResponse
   lnrpc.LightningAddress
   scalapb.UnknownFieldSet))

(defn get-remote-instance
  ([url macaroon]
   (let [cert-file (Option/empty)
         cert-opt  (Option/empty)]
     (get-remote-instance url macaroon cert-file cert-opt)))
  ([url macaroon cert-file cert-opt]
   (log/finer :get-remote-instance/creating
              {:url url :macaroon macaroon :cert-file cert-file :cert-opt cert-opt})
   (LndInstanceRemote. url macaroon cert-file cert-opt)))

(extend-type Chain
  Recordable
  (->record [this]
    (log/info :Chain/->record {:this this})
    {:chain              (.chain this)
     :network            (.network this)
     #_#_:unknown-fields (.unknownFields this)}))

(extend-type GetInfoResponse
  Recordable
  (->record [this]
    {:alias        (.alias this)
     :block-hash   (.blockHash this)
     :block-height (.blockHeight this)
     :chains       (map cs/->record (cs/vector->vec (.chains this)))
     :version      (.version this)
     :commit-hash  (.commitHash this)}))

(defn get-remote-client
  [^LndInstance i]
  (LndRpcClient/apply i (Option/empty)))

(defn get-info
  "Fetch node info from the remote node"
  ^GetInfoResponse [^LndRpcClient client]
  (log/info :get-info/starting {})
  (let [response (.getInfo client)]
    (log/info :get-info/response {:response response})
    (let [f (cs/await-future response)]
      (log/info :get-info/awaited {:f f})
      (let [result-data (async/<!! f)]
        (log/info :get-info/results {:result-data result-data})
        (if (instance? Throwable result-data)
          (do
            (log/info :get-info/throwable {:result-data result-data})
            (throw result-data))
          (let [{:keys [passed result]} result-data]
            (if passed
              (do
                (log/info :get-info/passed {})
                result)
              (do
                (log/info :get-info/failed {})
                (throw result)))
            #_(:result result-data)))))))

;; (defn list-payments
;;   [client]
;;   (:result (async/<!! (cs/await-future (.getInfo client))))
;;   )

(defn ->lightning-address
  [host]
  (let [pubkey         ""
        ;; host           nil
        unknown-fields (UnknownFieldSet/empty)]
    (LightningAddress. pubkey host unknown-fields)))

(defn ->connect-peer-request
  [host]
  (let [addr           (Option/apply (->lightning-address host))
        perm           false
        timeout        0
        unknown-fields (UnknownFieldSet/empty)]
    (ConnectPeerRequest. addr perm timeout unknown-fields)))

(defn connect-peer
  [client]
  (log/info :connect-peer/starting {:client client}))

(defn await-throwable
  [response]
  (let [f           (cs/await-future response)
        result-data (async/<!! f)]
    (if (instance? Throwable result-data)
      (throw result-data)
      (let [{:keys [passed result]} result-data]
        (if passed
          result (throw result))))))

(defn get-new-address
  "See: https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#getNewAddress:scala.concurrent.Future[org.bitcoins.core.protocol.BitcoinAddress]"
  [^LndRpcClient client]
  (await-throwable (.getNewAddress client)))

(defn unlock-wallet
  "See: https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#unlockWallet(password:String):scala.concurrent.Future[Unit]"
  [^LndRpcClient client ^String passphrase]
  (log/info :unlock-wallet/starting {:client client :passphrase passphrase})
  (await-throwable (.unlockWallet client passphrase)))

(defn initialize!
  "See: https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#initWallet(password:String):scala.concurrent.Future[com.google.protobuf.ByteString]"
  [^LndRpcClient client ^String password]
  (log/info :initialize!/starting {:client client :password password})
  (.initWallet client password))
