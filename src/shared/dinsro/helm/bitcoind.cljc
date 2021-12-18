(ns dinsro.helm.bitcoind
  (:require
   [clojure.string :as string]))

(defn ->bitcoin-conf
  [options]
  (let [{{_rpc-user :user
          _rpc-password :password} :rpc} options]
    (string/join
     "\n"
     ["regtest=1"
      "server=1"
      "txindex=1"
      "printtoconsole=1"
      "blockfilterindex=1"
      "txindex=1"
     ;; (str "rpcuser=" rpc-user)
     ;; (str "rpcpassword=" rpc-password)
      "rpcauth=rpcuser:3de4eb23a68a288cfbc857d3cf52b5c4$0b28c21a8d32d047b4da6b4b5f290951319bad3cb0985ef863c8fa4614f3c109"
      "rpcallowip=0.0.0.0/0"
      "whitelist=0.0.0.0/0"
      "zmqpubrawblock=tcp://0.0.0.0:28332"
      "zmqpubrawtx=tcp://0.0.0.0:28333"
      "zmqpubhashblock=tcp://0.0.0.0:28334"
      "[regtest]"
     ;; "rpcbind=127.0.0.1"
      "rpcbind=0.0.0.0"
     ;; "rpcbind=bitcoin"
      ])))

(defn merge-defaults
  [options]
  (let [{:keys [rpc name]
         :or
         {name "default"
          rpc {}}}             options
        {rpc-user :user
         rpc-password :password
         :or
         {rpc-user "rpcuser"
          rpc-password "rpcpassword"}} rpc]
    {:name name
     :rpc {:user rpc-user
           :password rpc-password}}))

(defn ->values
  [options]
  (let [options (merge-defaults options)
        {:keys [name]} options]
    (comment (str "bitcoin-" name))
    {:fullnameOverride  "bitcoin"
     :image {:repository "ruimarinho/bitcoin-core"
             :tag "22"}
     :configurationFile
     {"bitcoin.conf" (->bitcoin-conf options)}}))