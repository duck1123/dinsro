(ns dinsro.client.lnd-s
  "Clojure interop for Bitcoin-S LND client"
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clojure.core.async :as async]
   [dinsro.client.scala :as cs :refer [Recordable]]
   [lambdaisland.glogc :as log]
   [ring.util.codec :refer [base64-decode]])
  (:import
   java.net.URI
   org.bitcoins.lnd.rpc.config.LndInstanceRemote
   org.bitcoins.lnd.rpc.config.LndInstance
   org.bitcoins.lnd.rpc.LndRpcClient
   scala.Option
   lnrpc.Chain
   lnrpc.ConnectPeerRequest
   lnrpc.GetInfoResponse
   lnrpc.LightningAddress
   scalapb.UnknownFieldSet))

(def bob-cert
  "-----BEGIN CERTIFICATE-----
MIICezCCAiGgAwIBAgIQTeOjoj/ubeyJD7VEGVC2mTAKBggqhkjOPQQDAjBDMR8w
HQYDVQQKExZsbmQgYXV0b2dlbmVyYXRlZCBjZXJ0MSAwHgYDVQQDExdib2ItbG5k
LTc1Zjc3Njc2ZC1nYnN2cjAeFw0yMjA0MjkxNTE2MjFaFw0yMzA2MjQxNTE2MjFa
MEMxHzAdBgNVBAoTFmxuZCBhdXRvZ2VuZXJhdGVkIGNlcnQxIDAeBgNVBAMTF2Jv
Yi1sbmQtNzVmNzc2NzZkLWdic3ZyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE
bjoNJjql3sodXCZ6mBakLRU8SqT+TvQFJ5NlSzF2G9su/Zo9NN0qPTEBnLc2i/Sw
GivkfFg4xI9b3KBfFf3tFaOB9jCB8zAOBgNVHQ8BAf8EBAMCAqQwEwYDVR0lBAww
CgYIKwYBBQUHAwEwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUiOj/a/26jIKM
SPgLm668V8seJW8wgZsGA1UdEQSBkzCBkIIXYm9iLWxuZC03NWY3NzY3NmQtZ2Jz
dnKCCWxvY2FsaG9zdIIZbG5kLmJvYi5zdmMuY2x1c3Rlci5sb2NhbIIEdW5peIIK
dW5peHBhY2tldIIHYnVmY29ubocEfwAAAYcQAAAAAAAAAAAAAAAAAAAAAYcECioA
X4cQ/oAAAAAAAABUnxP//p8TrocEAAAAADAKBggqhkjOPQQDAgNIADBFAiEAwqOo
HUcY4qptDzAjtZ1FpDqFoWhR7JokQvXraK6bbyACICmycgFPcxRzpM7AynbqnIFr
ZEw+de+2IU8TFQ4JWo9Y
-----END CERTIFICATE-----")

(def bob-macaroon
  (str
   "AgEDbG5kAvgBAwoQmsrEBJEPx1X7PO46nmoPdRIBMBoWCgdhZGRyZXNzEgR"
   "yZWFkEgV3cml0ZRoTCgRpbmZvEgRyZWFkEgV3cml0ZRoXCghpbnZvaWNlcx"
   "IEcmVhZBIFd3JpdGUaIQoIbWFjYXJvb24SCGdlbmVyYXRlEgRyZWFkEgV3c"
   "ml0ZRoWCgdtZXNzYWdlEgRyZWFkEgV3cml0ZRoXCghvZmZjaGFpbhIEcmVh"
   "ZBIFd3JpdGUaFgoHb25jaGFpbhIEcmVhZBIFd3JpdGUaFAoFcGVlcnMSBHJ"
   "lYWQSBXdyaXRlGhgKBnNpZ25lchIIZ2VuZXJhdGUSBHJlYWQAAAYgr9elVJ"
   "2PWZVVxxLOtFYda319lftE96nIKgjG0zMXPYs="))

(defn get-remote-instance
  ([]
   (let [host "lnd.bob.svc.cluster.local"
         port 10009
         url  (URI. (str "https://" host ":" port "/"))]
     (get-remote-instance url)))
  ([url]
   (let [macaroon (bytes->hex (base64-decode bob-macaroon))]
     (get-remote-instance url macaroon)))
  ([url macaroon]
   (let [cert-file (Option/empty)
         cert-opt  (Option/apply bob-cert)]
     (get-remote-instance url macaroon cert-file cert-opt)))
  ([url macaroon cert-file cert-opt]
   (log/info :get-remote-instance/creating
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

(comment

  (->lightning-address "lnd.bob:9735")
  (->connect-peer-request "lnd.bob:9735")

  (def instance (get-remote-instance))

  (LndRpcClient. instance (Option/empty))

  (def client (get-remote-client instance))
  (async/<!! (cs/await-future (.connectPeer client (->connect-peer-request "lnd.bob:9735"))))

  (async/<!! (cs/await-future (.getInfo client)))

  (def result (get-info client))
  (.alias result)

  bob-cert

  (Option/apply bob-cert)

  nil)
