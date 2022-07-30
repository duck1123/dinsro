(ns dinsro.helm.specter
  (:require
   #?(:clj [cheshire.core :as json2])
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(defn ->node-config
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or   {rpcuser     "rpcuser"
                rpcpassword "rpcpassword"}} options]
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
     :fullpath      (str "/data/.specter/nodes/" name ".json")}))

(defn merge-defaults
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or
         {name        "foo"
          alias       "bar"
          rpcuser     "rpcuser"
          rpcpassword "rpcpassword"
          port        18443
          host        (str "bitcoin." name)}} options]

    {:name        name
     :alias       alias
     :rpcuser     rpcuser
     :rpcpassword rpcpassword
     :port        port
     :host        host}))

(defn ->values
  [{:keys [name] :as options}]
  (let [options (merge-defaults options)
        host    (str "specter." name ".localhost")]
    {:image        {:tag "v1.10.3"}
     :ingress      {:hosts [{:host  host
                             :paths [{:path "/"}]}]}
     :persistence  {:storageClassName "local-path"}
     :walletConfig #?(:clj (json2/encode (->node-config options))
                      :cljs (do (comment options) "{}"))}))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->values (merge-defaults options))))
