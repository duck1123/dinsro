(ns dinsro.commands
  (:require [babashka.tasks :refer [clojure shell]]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]))

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
  (let [cmd (->> cmds
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

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException _e
      #_(printf "Couldn't open '%s': %s\n" source (.getMessage e))
      nil)
    (catch RuntimeException _e
      #_(printf "Error parsing edn file '%s': %s\n" source (.getMessage e))
      nil)))

(defn ->tilt-config
  []
  (let [defaults (load-edn "site-defaults.edn")
        overrides (load-edn "site.edn")
        data (merge defaults overrides)]
    (println (json/generate-string data))))
