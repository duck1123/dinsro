(ns dinsro.helm.rtl
  #?(:clj (:require [cheshire.core :as json2])))

(defn make-node
  [options]
  (let [{name :name}  options
        node-name     (str "Node " name)
        macaroon-path (str "/mnt/data/" name)
        backup-path   (str "/mnt/data/" name "/backups")
        server-url    (str "https://lnd." name ".svc.cluster.local:8080")]
    {:index            1
     :lnNode           node-name
     :lnImplementation "LND"
     :Authentication   {:macaroonPath macaroon-path}
     :Settings         {:userPersona       "MERCHANT"
                        :themeMode         "NIGHT"
                        :themeColor        "PURPLE"
                        :channelBackupPath backup-path
                        :enableLogging     true
                        :lnServerUrl       server-url
                        :fiatConversion    false}}))

(defn ->config
  [options]
  (let [{name :name}     options
        multipass-hashed "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7"
        port             3000
        nodes            [{:name name}]]
    {:multiPassHashed  multipass-hashed
     :port             port
     :defaultNodeIndex 1,
     :sso              {:rtlSSO             0
                        :rtlCookiePath      ""
                        :logoutRedirectLink ""}
     :nodes            (map make-node nodes)}))

(defn merge-defaults
  [options]
  (let [{:keys [name]
         :or
         {name "1"}} options]
    {:name name}))

(defn ->values
  [options]
  (let [options      (merge-defaults options)
        config       (->config options)
        {name :name} options]
    {:certDownloader    {:image {:repository "duck1123/cert-downloader"
                                 :pullPolicy "Always"}}
     :ingress           {:hosts [{:host  (str "rtl." name ".localhost")
                                  :paths [{:path "/"}]}]}
     :configurationFile #?(:clj (json2/encode config) :cljs (do (comment config) ""))
     :bitcoin           {:host (str "bitcoin." name)}}))

(comment
  (->values {})

  (->config {})

  nil)
