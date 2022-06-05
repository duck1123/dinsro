#!/usr/bin/env bb
#_" -*- mode: clojure; -*-"

(require '[babashka.deps :as deps])
(require '[clojure.tools.cli :refer [parse-opts]])
(deps/add-deps '{:deps {djblue/portal {:mvn/version "0.22.1"}}})
(require '[portal.api :as p])

(def cli-options
  [[nil  "--host HOSTNAME" "Hostname" :default "localhost"]
   ["-p" "--port PORT"     "Port"     :default 5678]
   [nil  "--dry-run"       "Dry Run"  :default false]
   [nil  "--help"          "Help"]])

(def o (:options (parse-opts *command-line-args* cli-options)))
(prn o)

(when-not (:dry-run o)
  (p/start {:port (:port o) :host (:host o)})
  (println "Starting server")
  @(promise))
