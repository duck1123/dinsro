(ns dinsro.helm.lnd
  (:require
   #?(:clj [clj-yaml.core :as yaml])
   [clojure.string :as string]
   #?(:cljs [dinsro.yaml :as yaml])))

(defn merge-defaults
  [options]
  (let [{:keys [name alias auto-unlock chain ingress rpc tls]
         :or
         {name        "one"
          alias       "Node One"
          auto-unlock {}
          chain       :regtest
          ingress     {}
          rpc         {}
          tls         {}}}                                    options
        {auto-unlock-password :password
         :or
         {auto-unlock-password "password12345678"}}           auto-unlock
        {ingress-host :host
         :or
         {ingress-host (str "lnd." name ".locahost")}}        ingress
        {rpc-host     :host
         rpc-port     :port
         rpc-user     :user
         rpc-password :password
         :keys        [zmqpubrawblock zmqpubrawtx]
         :or
         {rpc-host       (str "bitcoin.bitcoin-" name)
          rpc-port       18443
          rpc-user       "rpcuser"
          rpc-password   "rpcpassword"
          zmqpubrawblock {}
          zmqpubrawtx    {}}}                                 rpc
        {zmqpubrawblock-host :host
         zmqpubrawblock-port :port
         :or
         {zmqpubrawblock-host rpc-host
          zmqpubrawblock-port 28332}}                         zmqpubrawblock
        {zmqpubrawtx-host :host
         zmqpubrawtx-port :port
         :or
         {zmqpubrawtx-host rpc-host
          zmqpubrawtx-port 28333}}                            zmqpubrawtx
        {tls-domains :domains
         :or
         {tls-domains [(str name "-lnd-internal." name ".svc.cluster.local")]}} tls]
    {:alias       alias
     :auto-unlock {:password auto-unlock-password}
     :chain       chain
     :ingress     {:host ingress-host}
     :rpc         {:host           rpc-host
                   :port           rpc-port
                   :user           rpc-user
                   :password       rpc-password
                   :zmqpubrawblock {:host zmqpubrawblock-host
                                    :port zmqpubrawblock-port}
                   :zmqpubrawtx    {:host zmqpubrawtx-host
                                    :port zmqpubrawtx-port}}
     :tls         {:domains tls-domains}}))

(defn bitcoin-section
  [options]
  (let [{:keys [chain]} options
        mainnet         (= chain :mainnet)
        testnet         (= chain :testnet)
        regtest         (= chain :regtest)
        bitcoin-header  "[Bitcoin]"
        bitcoin-lines   ["bitcoin.active=1"
                         (str "bitcoin.mainnet=" (if mainnet "1" "0"))
                         (str "bitcoin.testnet=" (if testnet "1" "0"))
                         (str "bitcoin.regtest=" (if regtest "1" "0"))
                         "bitcoin.node=bitcoind"]]
    (string/join "\n" (concat [bitcoin-header] bitcoin-lines))))

(defn ao-section
  [options]
  (let [{:keys [alias tls]}  options
        {tls-domains :domains} tls
        ao-header            "[Application Options]"
        ao-lines             (concat
                              ["debuglevel=info"
                               "restlisten=0.0.0.0:8080"
                               "rpclisten=0.0.0.0:10009"
                               "tlsextraip=0.0.0.0"
                               (str "alias=" alias)]
                              (map #(str "tlsextradomain=" %) tls-domains))]
    (string/join "\n" (concat [ao-header] ao-lines))))

(defn bitcoind-section
  [options]
  (let [{:keys [rpc]}                               options
        {rpc-host     :host
         rpc-port     :port
         rpc-user     :user
         rpc-password :password
         :keys        [zmqpubrawblock zmqpubrawtx]} rpc
        {zmqpubrawblock-host :host
         zmqpubrawblock-port :port}                 zmqpubrawblock
        {zmqpubrawtx-host :host
         zmqpubrawtx-port :port}                    zmqpubrawtx
        bitcoind-header                             "[Bitcoind]"
        bitcoind-lines                              [(str "bitcoind.rpchost=" (str rpc-host ":" rpc-port))
                                                     (str "bitcoind.rpcuser=" rpc-user)
                                                     (str "bitcoind.rpcpass=" rpc-password)
                                                     (str "bitcoind.zmqpubrawblock="
                                                          (str "tcp://" zmqpubrawblock-host ":" zmqpubrawblock-port))
                                                     (str "bitcoind.zmqpubrawtx="
                                                          (str "tcp://" zmqpubrawtx-host ":" zmqpubrawtx-port))]]
    (string/join "\n" (concat [bitcoind-header] bitcoind-lines))))

(defn ->lnd-config
  [options]
  (string/join
   "\n\n"
   [(bitcoin-section options)
    (bitcoind-section options)
    (ao-section options)]))

(defn ->value-options
  [{:keys [name]}]
  (let [alias           (str "Node " name)
        external-host   (str name "-lnd-external.default.svc.cluster.local")
        internal-host   (str name "-lnd-internal." name ".svc.cluster.local")
        bitcoin-host    (str "bitcoin." name)
        unlock-password "unlockpassword"]
    {:alias       alias
     :auto-unlock {:password unlock-password}
     :chain       :regtest
     :ingress     {:host external-host}
     :name        name
     :rpc         {:host bitcoin-host}
     :tls         {:domains [internal-host external-host]}}))

(defn ->values
  "Produce a lnd helm values file"
  [options]
  (let [options                                     (merge-defaults options)
        {:keys [auto-unlock chain ingress]}         options
        {ingress-host :host}                        ingress
        {auto-unlock-password :password
         :or
         {auto-unlock-password "password12345678"}} auto-unlock
        conf                                        (->lnd-config options)]
    {:configurationFile  {:lnd.conf conf}
     :loop               {:enable false}
     :pool               {:enable false}
     :persistence        {:enabled true}
     :autoUnlock         false
     :autoUnlockPassword auto-unlock-password
     :network            (name chain)
     :ingress            {:host ingress-host}}))

;; (defn ->values
;;   [options]
;;   (let [options (merge-defaults options)]
;;     {:configurationFile
;;      {"lnd.conf" (->lndconf options)}
;;      :loop {:enable false}
;;      :pool {:enable false}
;;      :persistence {:enabled true}
;;      :autoUnlock false
;;      :autoUnlockPassword "password12345678"
;;      :network "regtest"
;;      :ingress {:host "lnd1.localhost"}
;;      }))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->values (->value-options options))))
