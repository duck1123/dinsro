(ns dinsro.test-helpers
  (:require
   [dinsro.config :refer [secret]]
   [dinsro.components.config :as config]
   [dinsro.components.crux :as c.crux]
   [mount.core :as mount]
   [taoensso.timbre :as log]))

(defn start-db
  [f _schemata]
  (mount/stop #'c.crux/crux-nodes)
  (mount/start
   #'config/config
   #'secret
   #'c.crux/crux-nodes)
  (f))
