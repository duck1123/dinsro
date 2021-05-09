(ns dinsro.components.config
  (:require
   [com.fulcrologic.fulcro.server.config :as fserver]
   [dinsro.lib.logging :as logging]
   [mount.core :refer [defstate args]]
   [taoensso.timbre :as log])
  (:import java.io.File))

(defn get-config-path
  []
  (let [paths ["config/app.edn"
               "config.edn"]
        files (concat (map (fn [path]
                             (when path
                               (let [file (File. path)]
                                 (when (.exists file)
                                   (.getAbsolutePath file)))))
                           paths)
                      paths)
        files (filter identity files)]
    (first files)))

(defstate config
  "The overrides option in args is for overriding
   configuration in tests."
  :start
  (let [{:keys [overrides]} (args)
        loaded-config       (merge (fserver/load-config!
                                    {:config-path   (get-config-path)
                                     :defaults-path "config/defaults.edn"})
                                   overrides)]
    (logging/configure-logging! loaded-config)
    #_(println (logging/p loaded-config))
    loaded-config))
