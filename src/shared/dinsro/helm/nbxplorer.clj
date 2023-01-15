(ns dinsro.helm.nbxplorer)

(defn merge-defaults
  [options]
  (let [{:keys [host rpcurl nodeEndpoint]
         :or
         {host         "nbxplorer.localtest.me"
          rpcurl       "http://bitcoin-bitcoind.bitcoin3:18443"
          nodeEndpoint "bitcoin-bitcoind.bitcoin3:18444"}} options]
    {:host         host
     :rpcurl       rpcurl
     :nodeEndpoint nodeEndpoint}))

(defn ->values
  [options]
  (let [options                            (merge-defaults options)
        {:keys [host rpcurl nodeEndpoint]} options]
    {:config  {:rpcauth "rpcuser:rpcpassword"}
     :ingress {:enabled true
               :hosts   [{:host  host
                          :paths [{:path "/"}]}]}
     :nbxplorer
     {:network      "regtest"
      :rpcurl       rpcurl
      :nodeEndpoint nodeEndpoint}}))

(defn ->value-options
  [{:keys [name]}]
  {:host         (str "nbxplorer." name ".localtest.me")
   :rpcurl       (str "http://bitcoin." name ":18443")
   :nodeEndpoint (str "bitcoin." name ":18444")})
