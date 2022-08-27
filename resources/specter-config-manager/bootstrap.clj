(ns specter-config-manager.bootstrap
  (:require
   [babashka.curl :as curl]
   [babashka.fs :as fs]
   [babashka.tasks :refer [clojure shell]]
   [clojure.java.io :as io]))

(def config-path "/data-config/")
(def target-path "/data/")

;; set -x
;; ls -al /data-config
;; mkdir -p /data/.specter/nodes/
;; cp /data-config/default.json /data/.specter/nodes/default.json

(defn await-target-path
  []
  (let [node-path (str target-path ".specter/nodes")]
    ;; (fs/create-dirs node-path)
    (while (not (fs/exists? node-path))
      (println (str "waiting for target path: " node-path))
      (Thread/sleep 1000))
    (println "waited")
    (fs/copy "/data-config/default.json" "/data/.specter/nodes/default.json"
             {:replace-existing true})
    (shell (str "ls -al " (str target-path ".specter/nodes")))))

(defn -main
  []
  (println "Starting config manager")
  (await-target-path)
  (loop []
    (Thread/sleep 3600)
    (recur)))

(-main)
