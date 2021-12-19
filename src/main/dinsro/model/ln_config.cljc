(ns dinsro.model.ln-config
  (:require
   [clojure.string :as string]
   [yaml.core :as yaml]))

(defn ->yaml
  [o]
  (yaml/generate-string o))

(defn ->lnd-values
  "Produce a lnd helm values file"
  [{:keys [alias auto-unlock chain rpc ingress tls]
    :as   _options
    :or   {alias       "Node One"
           auto-unlock {}
           chain       :regtest
           ingress     {}
           rpc         {}}}]
  (let [{tls-domain :domain
         :or
         {tls-domain "lnd1-internal.lnd1.svc.cluster.local"}} tls
        {ingress-host :host
         :or
         {ingress-host "lnd1.locahost"}}                      ingress
        {auto-unlock-password :password
         :or
         {auto-unlock-password "password12345678"}}           auto-unlock
        {rpc-host     :host
         rpc-port     :port
         rpc-user     :user
         rpc-password :password
         :keys        [zmqpubrawblock zmqpubrawtx]
         :or
         {rpc-host       "bitcoind.bitcoin"
          rpc-port       10009
          rpc-user       "rpcuser"
          rpc-password   "rpcpassword"
          zmqpubrawblock {}
          zmqpubrawtx    {}}}                                 rpc
        {zmqpubrawblock-host :host
         zmqpubrawblock-port :port
         :or
         {zmqpubrawblock-host (:host rpc)
          zmqpubrawblock-port 28332}}                         zmqpubrawblock
        {zmqpubrawtx-host :host
         zmqpubrawtx-port :port
         :or
         {zmqpubrawtx-host (:host rpc)
          zmqpubrawtx-port 28332}}                            zmqpubrawtx
        mainnet                                               (= chain :mainnet)
        testnet                                               (= chain :testnet)
        regtest                                               (= chain :regtest)
        bitcoin-header                                        "[Bitcoin]"
        bitcoind-header                                       "[Bitcoind]"
        bitcoin-lines                                         ["bitcoin.active=1"
                                                               (str "bitcoin.mainnet=" (if mainnet "1" "0"))
                                                               (str "bitcoin.testnet=" (if testnet "1" "0"))
                                                               (str "bitcoin.regtest=" (if regtest "1" "0"))
                                                               "bitcoin.node=bitcoind"]
        bitcoin-section                                       (string/join "\n" (concat [bitcoin-header] bitcoin-lines))
        bitcoind-lines                                        [(str "bitcoind.rpchost=" (str rpc-host ":" rpc-port))
                                                               (str "bitcoind.rpcuser=" rpc-user)
                                                               (str "bitcoind.rpcpass=" rpc-password)
                                                               (str "bitcoind.zmqpubrawblock="
                                                                    (str "tcp://" zmqpubrawblock-host ":" zmqpubrawblock-port))
                                                               (str "bitcoind.zmqpubrawtx="
                                                                    (str "tcp://" zmqpubrawtx-host ":" zmqpubrawtx-port))]
        bitcoind-section                                      (string/join "\n" (concat [bitcoind-header] bitcoind-lines))
        ao-header                                             "[Application Options]"
        ao-lines                                              ["debuglevel=info"
                                                               "restlisten=0.0.0.0:8080"
                                                               "rpclisten=0.0.0.0:10009"
                                                               (str "tlsextradomain=" tls-domain)
                                                               "tlsextraip=0.0.0.0"
                                                               (str "alias=" alias)]
        application-options                                   (string/join "\n" (concat [ao-header] ao-lines))
        conf                                                  (string/join "\n\n" [bitcoin-section
                                                                                   bitcoind-section
                                                                                   application-options])]
    (->yaml
     {:configurationFile  {:lnd.conf conf}
      :loop               {:enable false}
      :pool               {:enable false}
      :persistence        {:enabled true}
      :autoUnlock         false
      :autoUnlockPassword auto-unlock-password
      :network            (name chain)
      :ingress            {:host ingress-host}})))
