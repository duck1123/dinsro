(ns dinsro.commands
  (:require
   [babashka.tasks :refer [clojure shell]]
   [cheshire.core :as json]
   [clojure.string :as string]
   [clj-yaml.core :as yaml]
   [dinsro.helm.bitcoind :as h.bitcoind]
   [dinsro.helm.dinsro :as h.dinsro]
   [dinsro.helm.fileserver :as h.fileserver]
   [dinsro.helm.lnd :as h.lnd]
   [dinsro.helm.rtl :as h.rtl]
   [dinsro.helm.specter :as h.specter]
   [dinsro.site :as site]))

(def target-dir "target")

(defn cljfmt
  [paths]
  (clojure
   (format "-M:cljfmt check %s --indents indentation.edn"
           (string/join " " paths))))

(defn earthly
  ([cmd]
   (earthly cmd {}))
  ([cmd opts]
   (println opts)
   (let [flags (->> [(when (:interactive opts) "-i")
                     (when (:privileged opts) "-P")]
                    (filter identity)
                    (string/join " "))]
     (println flags)
     (shell
      (format "earthly %s +%s" flags (name cmd))))))

(defn eight
  []
  (shell
   (format
    "echo \"%s\""
    (string/join
     "\n"
     ;; From: https://codepen.io/andrewarchi/pen/eJZjej
     ["    _-=-_    "
      "   ´´´-```   "
      "  (  (O)  )  "
      "  []=__○_[]  "
      " ´ |    °  `  "
      "´-´ ° _---_ ` "
      "|    ´ □=□ `|"
      "|  ° |  -  || "
      "`    | □=□ ´´"
      " `--_____--´  "]))))

(defn get-docker-status
  [container-name]
  (:out (shell
         {:out :string}
         (format "docker inspect -f {{.State.Health.Status}} %s" container-name))))

(defn chown
  [user group path]
  (shell (format "chown -R %s:%s %s" user group path)))

(defn sudo
  ([cmd]
   (sudo "root" cmd))
  ([user cmd]
   (shell (format "sudo -u %s %s" user cmd))))

(defn mkdir
  [path]
  #_(println (str "mkdir " path))
  (shell (str "mkdir -p " path)))

(defn display-env
  []
  (shell "sh -c \"env | sort\""))

(defn migrate-env
  [names]
  (->> names
       (map (fn [name] (when-let [v (System/getenv name)] (str name "=" v))))
       (filter identity)))

(defn create-namespace
  [name]
  (shell (format "sh -c \"kubectl create namespace %s | true\"" name)))

(defn delete-namespace
  [name]
  (shell (format "sh -c \"kubectl delete namespace %s | true\"" name)))

(defn dispatch
  [cmds]
  (let [cmd          (->> cmds
                          (map #(str "\"" (string/replace % #"\"" "\\\\\"") "\""))
                          (string/join " "))
        full-command (str "-Mdispatch " cmd)]
    (clojure full-command)))

(defn helm-rtl
  [n]
  (let [path     "resources/helm/rtl/"
        filename (format "conf/%s/rtl_values.yaml" n)
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "--name-template=rtl-" n)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn helm-rtl2
  [n]
  (let [path     "resources/helm/rtl/"
        filename (format "conf/%s/rtl_values.yaml" n)
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "--name-template=rtl-" n)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn ->tilt-config
  []
  (let [data      (h.dinsro/merge-defaults (site/get-site-config))]
    (json/generate-string data)))

(defn generate-tilt-config
  []
  (let [config-string (->tilt-config)]
    (spit "target/tilt_config.json" config-string)))

(defn tap
  [data]
  (let [data (or data :true)]
    (dispatch [(str "(tap> " (pr-str data) ")")])))

(defn generate-dinsro-values
  []
  (let [data (site/get-site-config)
        dinsro-values (h.dinsro/->dinsro-config data)
        filename (str target-dir "/dinsro_values.yaml")
        value-yaml (yaml/generate-string dinsro-values)]
    (mkdir target-dir)
    (spit filename value-yaml)))

(defn clean-semantic
  []
  (shell "rm -rf semantic/dist")
  (shell "rm -rf semantic/gulpfile.js")
  (shell "rm -rf semantic/src/definitions")
  (shell "rm -rf semantic/src/semantic.less")
  (shell "rm -rf semantic/src/site/collections")
  (shell "rm -rf semantic/src/site/elements")
  (shell "rm -rf semantic/src/site/globals/reset.overrides")
  (shell "rm -rf semantic/src/site/globals/reset.variables")
  (shell "rm -rf semantic/src/site/globals/site.variables")
  (shell "rm -rf semantic/src/site/modules")
  (shell "rm -rf semantic/src/site/views")
  (shell "rm -rf semantic/src/theme.less")
  (shell "rm -rf semantic/src/themes")
  (shell "rm -rf semantic/tasks"))

(defn compile-cljs
  []
  (let [devtools-url "http://devtools2.dinsro.dev.kronkltd.net"
        data         {:devtools {:devtools-url devtools-url}}
        args         (format "-M:dev:notebooks:guardrails:shadow-cljs compile main --config-merge '%s'"
                             (pr-str data))]
    (clojure args)))

(defn dev-bootstrap
  []
  (let [user          "circleci"
        group         "circleci"
        src-path      "/var/lib/dinsro"
        cert-path     "/mnt/certs"
        envs          (->> (migrate-env ["DEVTOOLS_URL" "DINSRO_USE_NOTEBOOKS" "WATCH_SOURCES"])
                           (filter identity)
                           (string/join " "))
        bootstrap-cmd (str envs " bb dev-bootstrap-user")]
    (mkdir cert-path)
    (chown user group src-path)
    (chown user group cert-path)
    (sudo bootstrap-cmd)))

(defn dev-bootstrap-user
  []
  (let [watch-sources (boolean (System/getenv "WATCH_SOURCES"))]
    (println "=========================================================================================")
    (let [envs         (->> (migrate-env ["DEVTOOLS_URL" "DINSRO_USE_NOTEBOOKS"])
                            (filter identity)
                            (clojure.string/join " "))
          compile-cmd  "bb compile-cljs"
          watch-cmd    (str "sh -c \"" envs " bb watch-cljs\"")
          run-cmd      "bb run"]
      (when watch-sources
        (shell compile-cmd)
        (future (shell watch-cmd)))
      (shell run-cmd))))

(defn watch-cljs
  ([]
   (watch-cljs ["main" "workspaces"]))
  ([targets]
   (let [devtools-url    (or (System/getenv "DEVTOOLS_URL") "http://localhost:9630")
         use-guardrails? (or (System/getenv "USE_GUARDRAILS") false)
         data            {:devtools {:devtools-url devtools-url}}
         aliases         (filter identity ["dev"
                                           (when use-guardrails? "guardrails")
                                           "devcards"
                                           "shadow-cljs"])
         alias-str       (str "-M:" (string/join ":" aliases))
         config-str      (str "--config-merge '" (pr-str data) "'")
         target-str      (string/join " " targets)
         args            (string/join " " [alias-str "watch" target-str config-str])]
     (println args)
     (clojure args))))

(defn workspaces
  "Starts and watches for workspaces"
  []
  (watch-cljs "workspaces"))

(defn helm-nbxplorer
  [name]
  (let [path     "resources/helm/nbxplorer"
        filename (str "target/conf/" name "/nbxplorer_values.yaml")
        cmd      (string/join
                  " "
                  ["helm template"
                   (str "-n " name)
                   (str "--values " filename)
                   (format "--namespace nbxplorer-%s" name)
                   "--name-template=nbxplorer"
                   path])]
    (shell cmd)))

(defn helm-fileserver
  [name]
  (let [path     "resources/helm/fileserver"
        filename (str "target/conf/" name "/fileserver_values.yaml")
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "-n " name)
                   (str "--name-template=" name)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn helm-bitcoin
  [name]
  (let [path     "resources/helm/fold/charts/bitcoind/"
        filename (str  "target/conf/" name "/bitcoind_values.yaml")
        cmd      (string/join
                  " "
                  ["helm template "
                   ;; "--create-namespace"
                   (str "-n " name)
                   (str "--name-template=" name)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn helm-dinsro
  []
  (let [name     "dinsro"
        path     "resources/helm/dinsro"
        filename "target/dinsro_values.yaml"
        cmd      (string/join
                  " "
                  ["helm template "
                   ;; "--create-namespace"
                   (str "-n " name)
                   (str "--name-template=" name)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn helm-lnd
  [name]
  (let [path     "resources/helm/fold/charts/lnd/"
        filename (str "target/conf/" name "/lnd_values.yaml")
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "-n " name)
                   (str "--name-template=" name)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn helm-specter
  [name]
  (let [path     "resources/helm/specter-desktop/"
        filename (format "target/conf/%s/specter_values.yaml" name)
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "-n " name)
                   (str "--name-template=specter-" name)
                   (str "--values " filename)
                   path])]
    (shell cmd)))

(defn generate-fileserver-values
  [name]
  (let [options (h.fileserver/->value-options {:name name})
        yaml    (yaml/generate-string (h.fileserver/->values options))]
    (mkdir (format "target/conf/%s" name))
    (spit (format "target/conf/%s/fileserver_values.yaml" name) yaml)))

(defn generate-lnd-values
  [name]
  (let [options (h.lnd/->value-options {:name name})
        yaml    (yaml/generate-string (h.lnd/->values options))]
    (mkdir (format "target/conf/%s" name))
    (spit (format "target/conf/%s/lnd_values.yaml" name) yaml)))

(defn generate-rtl-values
  [name]
  (let [options {:name name}
        yaml    (yaml/generate-string (h.rtl/->values options))]
    (mkdir (format "target/conf/%s" name))
    (spit (format "target/conf/%s/rtl_values.yaml" name) yaml)))

(defn generate-bitcoind-values
  [name]
  (let [options (h.bitcoind/->value-options {:name name})
        yaml    (yaml/generate-string (h.bitcoind/->values options))]
    (mkdir (str "target/conf/" name))
    (spit (str "target/conf/" name "/bitcoind_values.yaml") yaml)))

(defn generate-specter-values
  [name]
  (let [options {:name name}
        yaml    (yaml/generate-string (h.specter/->values options))]
    (mkdir (format "target/conf/%s" name))
    (spit (format "target/conf/%s/specter_values.yaml" name) yaml)))

(defn generate-values
  "generate-all-values"
  []
  (generate-dinsro-values)
  (generate-tilt-config)
  (let [data  (h.dinsro/merge-defaults (site/get-site-config))
        nodes (:nodes data)]
    (doseq [[name node-data] nodes]
      (let [{:keys [bitcoin lnd fileserver rtl specter #_lnbits]} node-data]
        (when bitcoin
          (println "generating bitcoin for " name)
          (generate-bitcoind-values name))
        (when lnd
          (println (str "generating lnd for " name))
          (generate-lnd-values name))
        (when fileserver
          (println (str "generating fileserver for " name))
          (generate-fileserver-values name))
        (when rtl
          (println (str "generating rtl for " name))
          (generate-rtl-values name))
        (when specter
          (println (str "generating specter for " name))
          (generate-specter-values name))
        #_(when lnbits
            (println (str "generating lnbits for " name))
            (generate-lnbits-values name))))))
