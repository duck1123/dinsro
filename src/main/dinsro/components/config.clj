(ns dinsro.components.config
  (:require
   ;; [com.fulcrologic.fulcro.server :as server]
   [com.fulcrologic.fulcro.server.config :refer [load-config!]]
   [mount.core :refer [defstate]]))

(defstate config :start (load-config! {:config-path "./config/dev.edn"}))


;; (defn make-system
;;   []
;;   (server/make-fulcro-server
;;    :config-path "/usr/local/etc/dinsro.edn"
;;    )
;;   )
