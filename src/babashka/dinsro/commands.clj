(ns dinsro.commands
  (:require
   [babashka.tasks :refer [clojure shell]]
   [cheshire.core :as json]
   [clj-yaml.core :as yaml]
   [clojure.string :as string]
   [dinsro.helm.bitcoind :as h.bitcoind]
   [dinsro.helm.dinsro :as h.dinsro]
   [dinsro.helm.fileserver :as h.fileserver]
   [dinsro.helm.lnd :as h.lnd]
   [dinsro.helm.nbxplorer :as h.nbxplorer]
   [dinsro.helm.nostr-rs-relay :as h.nostr-rs-relay]
   [dinsro.helm.nostream :as h.nostream]
   [dinsro.helm.rtl :as h.rtl]
   [dinsro.helm.specter :as h.specter]
   [dinsro.site :as site]))

(def target-dir "target")

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn cljfmt
  [paths]
  (clojure
   (format "-M:cljfmt check %s --indents indentation.edn"
           (string/join " " paths))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn display-env
  []
  (shell "sh -c \"env | sort\""))

(defn migrate-env
  [names]
  (->> names
       (map (fn [name] (when-let [v (System/getenv name)] (str name "=" v))))
       (filter identity)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn create-namespace
  [name]
  (shell (format "sh -c \"kubectl create namespace %s | true\"" name)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

(defn ->tilt-config
  []
  (let [data      (h.dinsro/merge-defaults (site/get-site-config))]
    (json/generate-string data)))

(defn generate-tilt-config
  []
  (let [config-string (->tilt-config)]
    (spit "target/tilt_config.json" config-string)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn compile-cljs
  []
  (let [devtools-url "http://devtools2.dinsro.dev.kronkltd.net"
        data         {:devtools {:devtools-url devtools-url}}
        args         (format "-M:dev:notebooks:guardrails:shadow-cljs compile main --config-merge '%s'"
                             (pr-str data))]
    (clojure args)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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
                                           "devcards"
                                           (when use-guardrails? "guardrails")
                                           "shadow-cljs"])
         alias-str       (str "-M:" (string/join ":" aliases))
         config-str      (str "--config-merge '" (pr-str data) "'")
         target-str      (string/join " " targets)
         args            (string/join " " [alias-str "watch" target-str config-str])]
     (println (str "clojure " args))
     (clojure args))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn workspaces
  "Starts and watches for workspaces"
  []
  (watch-cljs "workspaces"))

;; Generate Values

(def conf-dir "target/conf/")

(defn generate-fileserver-values
  [name]
  (let [options (h.fileserver/->value-options {:name name})
        yaml    (yaml/generate-string (h.fileserver/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/fileserver_values.yaml") yaml)))

(defn generate-lnd-values
  [name]
  (let [port-map {"alice" 9736 "bob" 9737}
        p2p-port (get port-map name 9735)
        options  (h.lnd/->value-options {:name name :p2p-port p2p-port})
        yaml     (yaml/generate-string (h.lnd/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/lnd_values.yaml") yaml)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn generate-nostr-rs-relay-values
  []
  (let [options {}
        yaml    (yaml/generate-string (h.nostr-rs-relay/->values options))]
    (mkdir (str conf-dir))
    (spit (str conf-dir "/nostr-rs-relay_values.yaml") yaml)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn generate-nostream-values
  []
  (let [options {}
        yaml    (yaml/generate-string (h.nostream/->values options))]
    (mkdir (str conf-dir))
    (spit (str conf-dir "/nostream_values.yaml") yaml)))

(defn generate-rtl-values
  [name]
  (let [options {:name name}
        yaml    (yaml/generate-string (h.rtl/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/rtl_values.yaml") yaml)))

(defn generate-bitcoind-values
  [name]
  (let [options (h.bitcoind/->value-options {:name name})
        yaml    (yaml/generate-string (h.bitcoind/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/bitcoind_values.yaml") yaml)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn generate-nbxplorer-values
  [name]
  (let [options (h.nbxplorer/->value-options {:name name})
        yaml    (yaml/generate-string (h.nbxplorer/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/nbxplorer_values.yaml") yaml)))

(defn generate-specter-values
  [name]
  (let [options {:name name}
        yaml    (yaml/generate-string (h.specter/->values options))]
    (mkdir (str conf-dir name))
    (spit (str conf-dir name "/specter_values.yaml") yaml)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

;; Helm Commands

(def use-prefix false)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-bitcoin
  [name]
  (let [repo     "https://chart.kronkltd.net/"
        chart    "bitcoind"
        version  "0.2.3"
        prefix   (if use-prefix "resources/helm/fold/charts/" "")
        path     (str prefix chart)
        filename (str  "target/conf/" name "/bitcoind_values.yaml")
        args     [(str "-n " name)
                  (str "--name-template=" name)
                  (str "--repo " repo)
                  (str "--values " filename)
                  (str "--version " version)]
        cmd      (string/join " " (concat ["helm template "] args [path]))]
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-dinsro
  []
  (let [repo     "https://chart.kronkltd.net/"
        chart    "dinsro"
        version  "0.1.0"
        name     chart
        prefix   (if use-prefix "resources/helm/" "")
        path     (str prefix chart)
        filename "target/dinsro_values.yaml"
        args     [(str "-n " name)
                  (str "--name-template=" name)
                  (str "--repo " repo)
                  (str "--values " filename)
                  (str "--version " version)]
        cmd      (string/join " " (concat ["helm template "] args [path]))]
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-fileserver
  [name]
  (let [use-prefix true
        prefix     (if use-prefix "resources/helm/" "")
        path       (str prefix "fileserver")
        filename   (str "target/conf/" name "/fileserver_values.yaml")
        args       [(str "-n " name)
                    (str "--name-template=" name)
                    (str "--repo " "https://chart.kronkltd.net/")
                    (str "--values " filename)]
        cmd        (string/join " " (concat ["helm template "] args [path]))]
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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
                   (str "--repo " "https://chart.kronkltd.net/")
                   path])]
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-lnd
  [name]
  (let [repo     "https://charts.foldapp.com"
        path     "lnd"
        version  "0.3.12"
        filename (str "target/conf/" name "/lnd_values.yaml")
        args     [(str "-n " name)
                  (str "--name-template=" name)
                  (str "--repo " repo)
                  (str "--values " filename)
                  (str "--version " version)]
        cmd      (string/join " " (concat ["helm template "] args [path]))]
    ;; (println cmd)
    (shell cmd)))

(def relay-use-prefix true)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-nostr-rs-relay
  []
  (let [use-local relay-use-prefix
        prefix    (if use-local "resources/helm/" "")
        repo      "https://chart.kronkltd.net/"
        chart     "nostr-rs-relay"
        path      (str prefix chart)
        version   "0.1.0"
        filename  "target/conf/nostr-rs-relay_values.yaml"
        args      (filter identity
                          [(when-not use-local (str "--repo " repo))
                           (when-not use-local (str "--version " version))
                           "--name-template=nostr-rs-relay"
                           (str "--values " filename)])
        cmd       (string/join " " (concat ["helm template "] args [path]))]
    ;; (println cmd)
    (shell cmd)))

(def nostream-use-prefix true)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-nostream
  []
  (let [use-local nostream-use-prefix
        prefix    (if use-local "resources/helm/" "")
        repo      "https://chart.kronkltd.net/"
        chart     "nostream"
        path      (str prefix chart)
        version   "0.1.0"
        filename  "target/conf/nostream_values.yaml"
        args      (filter identity
                          [(when-not use-local (str "--repo " repo))
                           (when-not use-local (str "--version " version))
                           "--name-template=nostream"
                           (str "--values " filename)])
        cmd       (string/join " " (concat ["helm template "] args [path]))]
    ;; (println cmd)
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-rtl
  [name]
  (let [use-local-rtl #_use-prefix true
        prefix         (if use-local-rtl "resources/helm/" "")
        repo           "https://chart.kronkltd.net/"
        chart          "rtl"
        path           (str prefix chart)
        version        "0.1.0"
        filename       (format "target/conf/%s/rtl_values.yaml" name)
        args           (filter identity
                               [(when-not use-local-rtl (str "--repo " repo))
                                (when-not use-local-rtl (str "--version " version))
                                (str "--name-template=" name)
                                (str "--values " filename)])
        cmd            (string/join " " (concat ["helm template "] args [path]))]
    ;; (println cmd)
    (shell cmd)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn helm-specter
  [name]
  (let [path     "specter-desktop"
        filename (format "target/conf/%s/specter_values.yaml" name)
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "-n " name)
                   (str "--repo " "https://chart.kronkltd.net/")
                   (str "--name-template=specter-" name)
                   (str "--values " filename)
                   path])]
    ;; (println cmd)
    (shell cmd)))
