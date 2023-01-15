(ns dinsro.helm.fileserver
  (:require
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(defn merge-defaults
  [options]
  (let [{:keys [name chain ingress persistence]
         :or
         {name    "one"
          chain   :regtest
          ingress {}}}                             options
        {ingress-host :host
         :or
         {ingress-host (str "lnd." name ".locahost")}} ingress
        {existing-claim :existingClaim
         :or
         {existing-claim (str name "-lnd")}}           persistence]
    {:chain       chain
     :ingress     {:host ingress-host}
     :persistence (merge {}
                         (when existing-claim {:existingClaim existing-claim}))}))

(defn ->values
  "Produce a lnd helm values file"
  [options]
  (let [options                                         (merge-defaults options)
        {:keys [chain ingress persistence]} options
        {ingress-host :host}                            ingress
        {existing-claim :existingClaim
         :or
         {existing-claim "foo"}}                        persistence]
    {:persistence        (merge
                          {:enabled true
                           ;; :storageClass "alice-lnd"
                           }
                          (when existing-claim {:existingClaim existing-claim}))
     :network            (name chain)
     :ingress            {:host ingress-host}}))

(defn ->value-options
  [{:keys [name]}]
  (let [alias           (str "Node " name)
        external-host   (str "lnd." name ".localtest.me")
        internal-host   (str "lnd." name ".svc.cluster.local")
        bitcoin-host    (str "bitcoin." name)
        unlock-password "unlockpassword"]
    {:alias       alias
     :auto-unlock {:password unlock-password}
     :chain       :regtest
     :ingress     {:host external-host}
     :persistence {:existingClaim (str name "-lnd")}
     :name        name
     :rpc         {:host bitcoin-host}
     :tls         {:domain internal-host}}))

(defn ->values-yaml
  [options]
  (let [vo (->value-options options)
        values (->values vo)]
    (yaml/generate-string values)))
