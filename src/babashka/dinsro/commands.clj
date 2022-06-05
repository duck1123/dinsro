(ns dinsro.commands
  (:require
   [babashka.tasks :refer [clojure shell]]
   [cheshire.core :as json]
   [clojure.string :as string]
   [clj-yaml.core :as yaml]
   [dinsro.helm.dinsro :as h.dinsro]
   [dinsro.helm.rtl :as h.rtl]
   [dinsro.site :as site]))

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

(defn ->tilt-config
  []
  (let [data      (h.dinsro/merge-defaults (site/get-site-config))]
    (println (json/generate-string data))))

(defn tap
  [data]
  (let [data (or data :true)]
    (dispatch [(str "(tap> " (pr-str data) ")")])))

(defn generate-dinsro-values
  []
  (let [{:keys [devtools-host ingress-host portal-host]} {}]
    {:database    {:enabled true}
     :devtools
     {:enabled true
      :ingress
      {:enabled true
       :hosts   [{:host  devtools-host
                  :paths [{:path "/"}]}]}}
     :notebook
     {:enabled true
      :ingress
      {:hosts [{:host  ingress-host
                :paths [{:path "/"}]}]}}
     :nrepl       {:enabled true}
     :persistence {:enabled true}
     :workspaces  {:enabled true}
     :portal
     {:ingress
      {:hosts
       [{:host  portal-host
         :paths [{:path "/"}]}]}}}))

(defn generate-rtl-values
  [n]
  (let [options {:name n}
        yaml    (yaml/generate-string (h.rtl/->values options))]
    (mkdir (format "conf/%s" n))
    (spit (format "conf/%s/rtl_values.yaml" n) yaml)))

(defn watch-cljs
  ([]
   (watch-cljs ["main" "workspaces"]))
  ([targets]
   (let [devtools-url    (or (System/getenv "DEVTOOLS_URL") "http://localhost:9630")
         use-guardrails? (or (System/getenv "USE_GUARDRAILS") false)
         data            {:devtools {:devtools-url devtools-url}}
         aliases         (filter identity ["dev" (when use-guardrails? "guardrails") "shadow-cljs"])
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

(defn helm-specter
  [n]
  (let [path     "resources/helm/specter-desktop/"
        filename (format "conf/%s/specter_values.yaml" n)
        cmd      (string/join
                  " "
                  ["helm template "
                   (str "--name-template=specter-" n)
                   (str "--values " filename)
                   path])]
    (shell cmd)))
