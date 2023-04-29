(ns dinsro.helm.dinsro
  (:require
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(def default-base-url "dinsro.localtest.me")

(defn merge-defaults
  [options]
  (let [{:keys       [devcards devtools docs
                      logLevel ingress
                      notebooks workspaces
                      local-devtools]
         base-url    :baseUrl
         production? :useProduction
         :or
         {base-url       default-base-url
          local-devtools false
          logLevel       :fine
          production?    false}} options

        {ingress-enabled? :enabled
         :or              {ingress-enabled? true}} ingress

        ;; devcards
        {devcards-enabled                :enabled
         devcards-declared-host          :host
         devcards-inherit-host           :inheritHost
         devcards-devtools-declared-host :devtools-host
         :or
         {devcards-enabled                false
          devcards-declared-host          (str "devcards." base-url)
          devcards-inherit-host           true
          devcards-devtools-declared-host (str "devtools.devcards." base-url)}} devcards

        ;; devtools
        {devtools-enabled? :enabled
         :or
         {devtools-enabled? true}} devtools

        ;; docs
        {docs-enabled? :enabled
         :or
         {docs-enabled? false}} docs

        ;; notebooks
        {notebooks-enabled?      :enabled
         notebooks-inherit-host  :inheritHost
         notebooks-declared-host :host
         :or
         {notebooks-enabled?      true
          notebooks-inherit-host  true
          notebooks-declared-host (str "notebooks." base-url)}} notebooks

        ;; workspaces
        {workspaces-enabled :enabled
         workspaces-host    :host
         :or
         {workspaces-enabled (not local-devtools)
          workspaces-host    (str "workspaces." base-url)}} workspaces

        devcards-host          (if devcards-inherit-host (str "devcards." base-url) devcards-declared-host)
        devcards-devtools-host (if devcards-inherit-host (str "devtools." devcards-host) devcards-devtools-declared-host)
        notebooks-host         (if notebooks-inherit-host (str "notebooks." base-url) notebooks-declared-host)
        devtools-host          (if local-devtools "localhost:9630" (str "devtools." base-url))
        image-tag              (if production? "latest" "dev-sources-latest")
        notebooks-url          (if notebooks-inherit-host (str "notebooks." base-url) notebooks-host)
        webtools-url           "localhost:9630"]
    (merge
     options
     {:base-url               base-url
      :devcards-enabled?      devcards-enabled
      :devcards-host          devcards-host
      :devcards-devtools-host devcards-devtools-host
      :devtools-enabled?      devtools-enabled?
      :devtools-host          devtools-host
      :docs-enabled?          docs-enabled?
      :log-level              (str logLevel)
      :image-tag              image-tag
      :ingress-enabled?       ingress-enabled?
      :notebooks-enabled?     notebooks-enabled?
      :notebooks-inherit-host notebooks-inherit-host
      :notebooks-host         notebooks-url
      :production             production?
      :webtools-url           webtools-url
      :workspaces-enabled?    workspaces-enabled
      :workspaces-host        workspaces-host})))

(defn ->dinsro-config
  [site-config]
  (let [{:keys
         [base-url
          devcards-enabled?
          devcards-host
          devcards-devtools-host
          devtools-enabled?
          devtools-host
          docs-enabled?
          image-tag
          ingress-enabled?
          log-level
          notebooks-enabled?
          notebooks-host
          webtools-url
          workspaces-enabled?
          workspaces-host]
         local-devtools :localDevtools
         seed           :seedDatabase
         nrepl-enabled? :useNrepl
         persistence    :usePersistence} (merge-defaults site-config)]
    {:replicaCount 1
     :logLevel     log-level

     :database
     {:enabled persistence}

     :devcards
     {:enabled devcards-enabled?
      :devtools
      {:enabled true
       :ingress
       {:enabled true
        :hosts   [{:host devcards-devtools-host :paths [{:path "/"}]}]}}

      :ingress
      {:hosts
       [{:host devcards-host :paths [{:path "/"}]}]}}

     :devtools
     {:enabled     devtools-enabled?
      :webtoolsUrl webtools-url
      :ingress     {:enabled (not local-devtools)
                    :hosts
                    [{:host devtools-host :paths [{:path "/"}]}]}}

     :docs
     {:enabled docs-enabled?}

     :image
     {:tag image-tag}

     :ingress
     {:enabled     ingress-enabled?
      :annotations {"cert-manager.io/cluster-issuer"           "letsencrypt-prod"
                    ;; "ingress.kubernetes.io/force-ssl-redirect" "true"
                    }
      :hosts       [{:host base-url :paths [{:path "/"}]}]
      :tls         [{:hosts [base-url] :secretName "dinsro-tls"}]}

     :notebooks
     {:enabled notebooks-enabled?
      :ingress
      {:enabled true
       :hosts   [{:host notebooks-host :paths [{:path "/"}]}]
       :tls     [{:hosts [notebooks-host] :secretName "dinsro-notebooks-tls"}]}}

     :nrepl
     {:enabled nrepl-enabled?}

     :persistence
     {:enabled persistence
      :seed    seed}

     :workspaces
     {:enabled workspaces-enabled?
      :ingress
      {:enabled true
       :hosts   [{:host workspaces-host :paths [{:path "/"}]}]
       :tls     [{:hosts [workspaces-host] :secretName "dinsro-workspaces-tls"}]}}}))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->dinsro-config (merge-defaults options))))
