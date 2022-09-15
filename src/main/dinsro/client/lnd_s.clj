(ns dinsro.client.lnd-s
  "Clojure interop for Bitcoin-S LND client"
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.init-wallet-request :as c.c.init-wallet-request]
   [dinsro.client.converters.list-accounts-request :as c.c.list-accounts-request]
   [dinsro.client.converters.list-channels-request :as c.c.list-channels-request]
   [dinsro.client.converters.list-accounts-response]
   [dinsro.client.scala :as cs :refer [Recordable]]
   [dinsro.specs :as ds]
   [erp12.fijit.try :as eft]
   [lambdaisland.glogc :as log])
  (:import
   com.google.protobuf.ByteString
   org.bitcoins.lnd.rpc.config.LndInstanceRemote
   org.bitcoins.lnd.rpc.config.LndInstance
   org.bitcoins.lnd.rpc.LndRpcClient
   scala.Option
   lnrpc.Chain
   lnrpc.ConnectPeerRequest
   lnrpc.GetInfoResponse
   lnrpc.LightningAddress
   scala.util.Failure
   walletrpc.WalletKitClient))

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

(>def ::client (ds/instance? LndRpcClient))
(>def ::walletkit-client (ds/instance? WalletKitClient))

(defn get-remote-client
  [^LndInstance i]
  (LndRpcClient/apply i (Option/empty)))

(>defn get-walletkit-client
  "https://bitcoin-s.org/api/walletrpc/WalletKitClient.html"
  [^LndRpcClient client]
  [::client => ::walletkit-client]
  (.wallet client))

(defn get-info
  "Fetch node info from the remote node"
  ^GetInfoResponse [^LndRpcClient client]
  (log/info :get-info/starting {})
  (let [response (.getInfo client)]
    (log/finer :get-info/response {:response response})
    (let [f (cs/await-future response)]
      (log/finer :get-info/awaited {:f f})
      (let [result-data (async/<!! f)]
        (log/finer :get-info/results {:result-data result-data})
        (if (instance? Throwable result-data)
          (do
            (log/info :get-info/throwable {:result-data result-data})
            (throw result-data))
          (let [{:keys [passed result]} result-data]
            (if passed
              (do
                (log/finer :get-info/passed {})
                result)
              (do
                (log/finer :get-info/failed {:result result})
                (let [_ex (eft/get result)]
                  (throw result))))))))))

(>defn ->lightning-address
  "https://bitcoin-s.org/api/lnrpc/LightningAddress.html"
  [^String host ^String pubkey]
  [string? string? => (ds/instance? LightningAddress)]
  (let [unknown-fields (cs/empty-unknown-field-set)]
    (LightningAddress. pubkey host unknown-fields)))

(>defn ->connect-peer-request
  "https://bitcoin-s.org/api/lnrpc/ConnectPeerRequest.html"
  [^String host ^String pubkey]
  [string? string?  => (ds/instance? ConnectPeerRequest)]
  (let [addr           (Option/apply (->lightning-address host pubkey))
        perm           false
        timeout        (cs/uint64 0)
        unknown-fields (cs/empty-unknown-field-set)
        obj            (ConnectPeerRequest. addr perm timeout unknown-fields)]
    (log/info :->connect-peer-request/finished {:obj obj})
    obj))

(defn await-throwable
  "Return the results of a throwable future"
  [response]
  (log/finer :await-throwable/starting {:response response})
  (let [f (cs/await-future response)]
    (log/finer :await-throwable/awaited {:f f})
    (let [result-data (async/<!! f)]
      (if (instance? Throwable result-data)
        (do
          (log/finer :await-throwable/throwable {:result-data result-data})
          (throw result-data))
        (let [{:keys [passed result]} result-data]
          (if passed
            (do
              (log/finer :await-throwable/passed {:passed passed :result result})
              result)
            (do
              (log/finer :await-throwable/not-passed {:passed passed :result result})
              (if (instance? Failure result)
                (let [o (.get result)]
                  (log/finer :await-throwable/failure {:o o})
                  (throw (RuntimeException. (pr-str o))))
                (throw (RuntimeException. (pr-str result)))))))))))

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
  [^LndRpcClient client ^String password mnemonic]
  (log/info :initialize!/starting {:client client :password password})
  (let [unlocker-client (.unlocker client)
        wallet-password (ByteString/copyFromUtf8 "passphrase12345678")
        request         (c.c.init-wallet-request/->request wallet-password mnemonic)]
    (log/info :initialize!/request-generated {:request request})
    (let [response (.initWallet unlocker-client request)]
      (log/info :initialize!/finished {:response response})
      (let [awaited-response (await-throwable response)]
        (log/info :initialize!/awaited {:awaited-response awaited-response})
        awaited-response))))

(>defn connect-peer!
  "See: https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#connectPeer(request:lnrpc.ConnectPeerRequest):scala.concurrent.Future[Unit]"
  [^LndRpcClient client  ^String host  ^String pubkey]
  [::client string? string? => any?]
  (log/info :connect-peer!/starting {:host host :pubkey pubkey})
  (let [request          (->connect-peer-request host pubkey)
        response         (.connectPeer client request)
        awaited-response (await-throwable response)]
    (log/info :connect-peer!/finished {:awaited-response awaited-response})
    awaited-response))

(>defn list-wallet-accounts
  [client]
  [::client => any?]
  (log/info :list-wallet-accounts/starting {})
  (let [wallet-kit-client (.wallet client)
        request           (c.c.list-accounts-request/->obj)
        response          (.listAccounts wallet-kit-client request)
        obj               (await-throwable response)]
    (log/info :list-wallet-accounts/finished {:obj obj})
    obj))

(>defn list-channels
  "https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#listChannels(request:lnrpc.ListChannelsRequest):scala.concurrent.Future[Vector[lnrpc.Channel]]"
  [client]
  [::client => any?]
  (log/info :fetch-channels!/starting {})
  (let [request (c.c.list-channels-request/->obj)]
    (.listChannels client request)))

(>defn get-node-info
  [_client pubkey]
  [::client string? => any?]
  (log/info :get-node-info/starting {:pubkey pubkey})
  (let [_response nil]
    (throw (RuntimeException. "not implemented"))))
