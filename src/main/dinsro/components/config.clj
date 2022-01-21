(ns dinsro.components.config
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [com.fulcrologic.fulcro.server.config :as fserver]
   [dinsro.lib.logging :as logging]
   [mount.core :refer [defstate args]]
   [ring.util.codec :refer [base64-encode base64-decode]]
   [taoensso.timbre :as log])
  (:import java.io.File
           java.io.FileNotFoundException))

(defn get-config-path
  []
  (let [paths    ["/etc/dinsro/config.edn"
                  "config/app.edn"
                  "config.edn"]
        get-path (fn [path]
                   (when path
                     (let [file (File. path)]
                       (when (.exists file)
                         (.getAbsolutePath file)))))
        files    (concat (map get-path paths) paths)
        files    (vec (filter (fn [path] (when-let [file (and path (File. path))]
                                           (.exists file))) files))]
    (first files)))

(defstate config
  "The overrides option in args is for overriding
   configuration in tests."
  :start
  (let [{:keys [config overrides]} (args)
        config-path (log/spy :info (or config (get-config-path) "config/prod.edn"))
        loaded-config (fserver/load-config! {:config-path config-path})
        merged-config (merge loaded-config overrides)]
    (logging/configure-logging! merged-config)
    (log/infof "Loading config: %s" config-path)
    merged-config))

(def default-secret-path ".secret")

(defn generate-secret
  []
  (random-bytes 32))

(defn write-secret
  [secret-path]
  (log/debug "Generating new secret")
  (spit secret-path (base64-encode (generate-secret))))

(defn read-secret
  [secret-path]
  (try (base64-decode (slurp secret-path))
       (catch FileNotFoundException _ex
         (log/warn "No secret found"))))

(defstate secret
  :start
  (or (config :secret)
      (let [secret-path (or (config ::secret-path) default-secret-path)]
        (or (read-secret secret-path)
            (do
              (write-secret secret-path)
              (read-secret secret-path))))))
