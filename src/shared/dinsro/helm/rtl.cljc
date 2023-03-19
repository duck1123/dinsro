(ns dinsro.helm.rtl
  #?(:clj (:require [cheshire.core :as json2])))

(defn make-node
  [options]
  (let [{name :name}  options
        node-name     (str "Node " name)
        macaroon-path (str "/mnt/data/" name)
        backup-path   (str "/mnt/data/" name "/backups")
        scheme        "http"
        port          "10009"
        namespace     "dinsro"
        service-name  (str name "-lnd-internal")
        server-url    (str scheme "://" service-name "." namespace ".svc.cluster.local:" port)]
    {:index            1
     :lnNode           node-name
     :lnImplementation "LND"
     :Authentication   {:macaroonPath macaroon-path}
     :Settings         {:userPersona       "MERCHANT"
                        :themeMode         "NIGHT"
                        :themeColor        "PURPLE"
                        :channelBackupPath backup-path
                        ;; :enableLogging     true
                        :logLevel          "DEBUG"
                        :lnServerUrl       server-url
                        :fiatConversion    false}}))

(defn ->config
  "See: https://github.com/Ride-The-Lightning/RTL/blob/master/Sample-RTL-Config.json"
  [options]
  (let [{name :name}     options
        multipass-hashed "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7"
        port             3000
        nodes            [{:name name}]]
    {;; :multiPass "hunter2"
     :multiPassHashed  multipass-hashed
     :port             port
     :defaultNodeIndex 1,
     :SSO              {:rtlSSO             0
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
    {:certDownloader    {:image {:repository "k3d-myregistry.localtest.me:12345/duck1123/cert-downloader"
                                 :tag "dev"
                                 :pullPolicy "Always"}}
     :image             {:tag "0.13.1"}
     ;; :image             {:tag "0.12.2"}
     ;; :image             {:tag "0.11.1"}
     :ingress           {:hosts [{:host  (str "rtl." name ".localtest.me")
                                  :paths [{:path "/"}]}
                                 {:host  (str "rtl." name ".dinsro.dev.kronkltd.net")
                                  :paths [{:path "/"}]}]}
     :configurationFile #?(:clj (json2/encode config) :cljs (do (comment config) ""))
     :bitcoin           {:host (str name "-bitcoind")}}))

(comment
  (->values {})

  (->config {})

  nil)
