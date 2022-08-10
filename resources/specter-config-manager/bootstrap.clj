(ns specter-config-manager.bootstrap
  (:require
   [babashka.curl :as curl]
   [babashka.fs :as fs]
   [babashka.tasks :refer [clojure shell]]
   [clojure.java.io :as io])
  )

(def config-path "/data-config/")
(def target-path "/data/")

;; set -x
;; ls -al /data-config
;; mkdir -p /data/.specter/nodes/
;; cp /data-config/default.json /data/.specter/nodes/default.json

(defn await-target-path
  []
  (while (not (fs/exists? (str target-path ".specter/nodes")))
    (println "waiting for target path")
    (Thread/sleep 1000)
    )
  (println "waited")
  (fs/copy "/data-config/default.json" "/data/.specter/nodes/default.json"
           {:replace-existing true}
           )
  (shell (str "ls -al " (str target-path ".specter/nodes"))))

(defn -main
  []
  (println "Starting config manager")
  (await-target-path)
  )

(-main)
