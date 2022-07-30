(ns dinsro.actions.core.node-base
  (:require
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.rpc.client.v22.BitcoindV22RpcClient
   java.net.URI))

(defn get-remote-uri
  [{::m.c.nodes/keys [host]}]
  (URI. (str "http://" host ":" "18444")))

(defn get-rpc-uri
  [{::m.c.nodes/keys [host port]}]
  (URI. (str "http://" host ":" port)))

(defn get-auth-credentials
  [{::m.c.nodes/keys [rpcuser rpcpass]}]
  (c.bitcoin-s/get-auth-credentials rpcuser rpcpass))

(defn get-remote-instance
  [node]
  (c.bitcoin-s/get-remote-instance
   (c.bitcoin-s/regtest-network)
   (get-remote-uri node)
   (get-rpc-uri node)
   (get-auth-credentials node)
   (c.bitcoin-s/get-zmq-config)))

;; See https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html

(defn get-client
  [node]
  (log/finer :get-client/starting {:node node})
  (let [instance (get-remote-instance node)]
    (BitcoindV22RpcClient/apply instance)))
