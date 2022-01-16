(ns dinsro.helm.specter)

(defn ->node-config
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]} options]
    {:name          name
     :alias         alias
     :autodetect    false
     :datadir       ""
     :user          rpcuser
     :password      rpcpassword
     :port          port
     :host          host
     :protocol      "http"
     :external_node true
     :fullpath      (format "/data/.specter/nodes/%s.json" name)}))

(defn merge-defaults
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or
         {name        "foo"
          alias       "bar"
          rpcuser     "rpcuser"
          rpcpassword "rpcpassword"
          port        18443
          host        "lnd-foo.lnd-foo"}} options]

    {:name        name
     :alias       alias
     :rpcuser     rpcuser
     :rpcpassword rpcpassword
     :port        port
     :host        host}))

(defn ->values
  [{:keys [name] :as options}]
  (let [options (merge-defaults options)]
    {:image        {:tag "v1.7.2"}
     :ingress      {:hosts [{:host  (str "specter-" name ".localhost")
                             :paths [{:path "/"}]}]}
     :persistence  {:storageClassName "local-path"}
     :walletConfig (prn-str (->node-config options))}))
