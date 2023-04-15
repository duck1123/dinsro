(ns dinsro.components.config
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [com.fulcrologic.fulcro.server.config :as fserver]
   [dinsro.lib.logging :as logging]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate args]]
   [ring.util.codec :refer [base64-encode base64-decode]])

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

(defn start-config!
  []
  (let [{:keys [config overrides]} (args)
        config-path                (or config (get-config-path) "config/prod.edn")
        loaded-config              (fserver/load-config! {:config-path config-path})
        merged-config              (merge loaded-config overrides)]
    (logging/configure-logging! merged-config)
    (log/info :config/starting {:config-path config-path})
    merged-config))

(defstate config-map
  "The overrides option in args is for overriding
   configuration in tests."
  :start (start-config!))

(defn get-config
  []
  @config-map)

(def default-secret-path ".secret")

(defn generate-secret
  []
  (random-bytes 32))

(defn write-secret
  [secret-path]
  (log/debug  :write-secret/starting {})
  (spit secret-path (base64-encode (generate-secret))))

(defn read-secret
  [secret-path]
  (try (base64-decode (slurp secret-path))
       (catch FileNotFoundException _ex
         (log/warn :read-secret/no-secret {}))))

(defstate secret
  :start
  (let [resolved-config (get-config)]
    (or (resolved-config :secret)
        (let [secret-path (or (resolved-config ::secret-path) default-secret-path)]
          (or (read-secret secret-path)
              (do
                (write-secret secret-path)
                (read-secret secret-path)))))))
