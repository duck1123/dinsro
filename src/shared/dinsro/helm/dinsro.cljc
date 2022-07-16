(ns dinsro.helm.dinsro
  (:require
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(def default-base-url "dinsro.localhost")

(defn merge-defaults
  [options]
  (let [local-devtools        false
        {:keys       [devcards devtools docs notebooks portal workspaces]
         base-url    :baseUrl
         production? :useProduction
         :or
         {base-url    default-base-url
          production? false}} options

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
        {devtools-enabled :enabled
         :or
         {devtools-enabled true}} devtools

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

        ;; portal
        {portal-enabled :enabled
         :or
         {portal-enabled false}} portal

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
      :devcards-enabled       devcards-enabled
      :devcards-host          devcards-host
      :devcards-devtools-host devcards-devtools-host
      :devtools-enabled       devtools-enabled
      :devtools-host          devtools-host
      :docs-enabled           docs-enabled?
      :image-tag              image-tag
      :notebooks-enabled      notebooks-enabled?
      :notebooks-inherit-host notebooks-inherit-host
      :notebooks-host         notebooks-url
      :portal-enabled         portal-enabled
      :production             production?
      :webtools-url           webtools-url
      :workspaces-enabled     workspaces-enabled
      :workspaces-host        workspaces-host})))

(defn ->dinsro-config
  [site-config]
  (let [{:keys
         [base-url
          devcards-enabled
          devcards-host
          devcards-devtools-host
          devtools-enabled
          devtools-host
          docs-enabled
          image-tag
          notebooks-enabled
          notebooks-host
          portal-enabled
          webtools-url
          workspaces-enabled
          workspaces-host]
         local-devtools :localDevtools
         portal-host    :portalHost
         seed           :seedDatabase
         nrepl          :useNrepl
         persistence    :usePersistence} (merge-defaults site-config)]
    {:replicaCount 1

     :database
     {:enabled persistence}

     :devcards
     {:enabled devcards-enabled
      :devtools
      {:enabled true
       :ingress
       {:enabled true
        :hosts
        [{:host devcards-devtools-host :paths [{:path "/"}]}]}}

      :ingress
      {:hosts
       [{:host devcards-host :paths [{:path "/"}]}]}}

     :devtools
     {:enabled     devtools-enabled
      :webtoolsUrl webtools-url
      :ingress     {:enabled (not local-devtools)
                    :hosts
                    [{:host devtools-host :paths [{:path "/"}]}]}}

     :docs
     {:enabled docs-enabled}

     :image
     {:tag image-tag}

     :ingress
     {:hosts [{:host  base-url
               :paths [{:path "/"}]}]}

     :notebooks
     {:enabled notebooks-enabled
      :ingress
      {:enabled true
       :hosts
       [{:host notebooks-host :paths [{:path "/"}]}]}}

     :nrepl
     {:enabled nrepl}

     :persistence
     {:enabled persistence
      :seed    seed}

     :portal
     {:enabled portal-enabled
      :service {:type "ClusterIP" :port 5678}
      :ingress {:enabled portal-enabled
                :hosts
                [{:host portal-host :paths [{:path "/"}]}]}}

     :workspaces
     {:enabled workspaces-enabled
      :ingress {:hosts
                [{:host workspaces-host :paths [{:path "/"}]}]}}}))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->dinsro-config (merge-defaults options))))
