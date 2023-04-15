(ns dinsro.test-helpers
  (:require
   [dinsro.components.config :as config  :refer [secret]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]
   [mount.core :as mount]))

(defn start-db
  [f schemata]
  (log/info :start-db/starting {:f f :schemata schemata})
  (mount/in-cljc-mode)
  (mount/stop #'c.xtdb/xtdb-nodes)
  (mount/start
   #'config/config-map
   #'secret
   #'c.xtdb/xtdb-nodes)
  (f))

(defmacro key-card [kw]
  `(nextjournal.devcards/defcard ~(symbol (str (name kw) "-card")) []
     [nextjournal.viewer/inspect (dinsro.specs/gen-key ~kw)]))
