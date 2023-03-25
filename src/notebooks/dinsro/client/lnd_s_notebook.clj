^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.client.lnd-s-notebook
  (:require
   [clojure.core.async :as async]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [erp12.fijit.collection :as efc]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.lnd.rpc.LndRpcClient
   scala.Option))

;; # LND Client

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; [client](https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html)

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

(def password "hunter2")

;; ## initialize! [link](https://bitcoin-s.org/api/org/bitcoins/lnd/rpc/LndRpcClient.html#initWallet(password:String):scala.concurrent.Future[com.google.protobuf.ByteString])

(comment
  (def instance nil #_(c.lnd-s/get-remote-instance))
  (def client (c.lnd-s/get-remote-client instance))
  (c.lnd-s/initialize! n.lnd/client password (efc/scala-list))

  nil)

(comment

  (c.lnd-s/->lightning-address "lnd-bob:9735" "")

  (def request (c.lnd-s/->connect-peer-request "lnd-bob:9735" ""))

  (LndRpcClient. instance (cs/none))

  (async/<!! (cs/await-future (.connectPeer client request)))

  (async/<!! (cs/await-future (.getInfo client)))

  (def result (c.lnd-s/get-info client))
  (.alias result)

  bob-cert

  (Option/apply bob-cert)

  nil)
