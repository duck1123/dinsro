(ns dinsro.test-helpers
  (:require
   #?(:clj [dinsro.components.config :as config  :refer [secret]])
   #?(:clj [dinsro.components.xtdb :as c.xtdb])
   [dinsro.specs :as ds]
   #?(:clj [mount.core :as mount])
   #?(:cljs [nextjournal.devcards :as dc]))
  #?(:cljs (:require-macros [dinsro.test-helpers])))

#?(:clj
   (defn start-db
     [f _schemata]
     (mount/stop #'c.xtdb/xtdb-nodes)
     (mount/start
      #'config/config
      #'secret
      #'c.xtdb/xtdb-nodes)
     (f)))

(defmacro key-card [kw]
  `(nextjournal.devcards/defcard ~(symbol (str (name kw) "-card")) []
     [nextjournal.viewer/inspect (dinsro.specs/gen-key ~kw)]))
