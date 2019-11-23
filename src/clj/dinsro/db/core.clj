(ns dinsro.db.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [datahike.config :as d.config]
            [dinsro.config :refer [env]]
            [mount.core :refer [defstate]]
            [taoensso.timbre :as timbre]))

(defstate ^:dynamic *conn*
  "The connection to the datahike database"
  :start (if-let [uri (env :datahike-url)]
           (do
             (when-not (d/database-exists? (d.config/uri->config uri))
               (timbre/info "Creating database: " uri)
               (d/create-database uri))
             (d/connect uri))
           (throw (ex-info "Could not find uri" {})))

  :stop (do
          (timbre/info "stopping real connection")
          (d/release *conn*)))

(defn create-database
  []
  (let [uri (env :datahike-url)]
    (d/create-database uri)))

(s/def :db/id int?)
(comment
  (gen/generate (s/gen :db/id))
  )

(s/def :db-pos/id pos-int?)
(comment
  (gen/generate (s/gen :db-pos/id))
  )
