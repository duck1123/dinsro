(ns dinsro.helm.nbxplorer)

(defn merge-defaults
  [options]
  (let [{:keys [host rpcurl nodeEndpoint]
         :or
         {host         "nbxplorer.localhost"
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
