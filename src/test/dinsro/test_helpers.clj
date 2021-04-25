(ns dinsro.test-helpers
  (:require
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.config :refer [secret]]
   [dinsro.components.config :as config]
   [dinsro.components.datahike :as db]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(defn start-db
  [f schemata]
  (mount/start #'config/config #'secret #'db/*conn*)
  (d/delete-database uri)
  (when-not (d/database-exists? (uri->config uri))
    (d/create-database uri))
  (with-redefs [db/*conn* (d/connect uri)]
    (doseq [schema schemata]
      (d/transact db/*conn* schema))
    (f)))
