(ns dinsro.components.config
  (:require
   [com.fulcrologic.fulcro.server.config :as fserver]
   [dinsro.lib.logging :as logging]
   [environ.core :refer [env]]
   [mount.core :refer [defstate args]]
   [taoensso.timbre :as timbre])
  (:import java.io.File))

(defstate config
  "The overrides option in args is for overriding
   configuration in tests."
  :start
  (let [{:keys [config overrides]} (args)
        config-file-path           (.getAbsolutePath
                                    (File. (or (env :config-file) "config.edn")))
        config-path                (or config config-file-path)
        loaded-config              (merge (fserver/load-config! {:config-path config-path})
                                          overrides)]
    (logging/configure-logging! loaded-config)
    loaded-config))
