(ns dinsro.test-helpers
  (:require
   [dinsro.components.config :as config  :refer [secret]]
   [dinsro.components.xtdb :as c.xtdb]
   [mount.core :as mount]))

(defn start-db
  [f _schemata]
  (mount/stop #'c.xtdb/xtdb-nodes)
  (mount/start
   #'config/config
   #'secret
   #'c.xtdb/xtdb-nodes)
  (f))
