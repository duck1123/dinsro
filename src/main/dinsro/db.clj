(ns dinsro.db
  (:require
   [clojure.java.io :as io]
   [datahike.api :as d]
   [datahike.config :as d.config]
   [datahike.db :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.components.config :as config]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as timbre])
  (:import java.io.File))

(def db-error-message
  "Database configuration not found, :datahike-url environment variable must be set before running")

(defn get-url
  []
  (let [datahike-url (config/config :datahike-url)
        data-path (config/config :data-path)]
    (or datahike-url
        (when data-path
          (let [file (File. data-path)]
            (when (.exists file)
              (str "datahike:file://" (.getAbsolutePath file))))))))

(defn initialize-db!
  [uri]
  (when-not (d/database-exists? (d.config/uri->config uri))
    (timbre/info "Creating database: " uri)
    (d/create-database uri)))

(defn start!
  []
  (if-let [uri (get-url)]
    (do
      (initialize-db! uri)
      (d/connect uri))
    (throw (ex-info db-error-message {}))))

(defstate ^:dynamic *conn*
  "The connection to the datahike database"
  :start (start!)
  :stop (do (timbre/info "stopping real connection")
            (d/release *conn*)))

(defn create-database
  []
  (let [uri (get-url)]
    (d/create-database uri)))

(defn delete-database
  []
  (let [uri (get-url)]
    (d/delete-database uri)))

(defn export-db
  "Export the database in a flat-file of datoms at path."
  [db path]
  (with-open [f (io/output-stream path)
              w (io/writer f)]
    (binding [*out* w]
      (doseq [d (datahike.db/-datoms db :eavt [])]
        (when (not= (:a d) :db/txInstant)
          (prn d))))))

(def schemata
  [m.accounts/schema
   m.categories/schema
   m.currencies/schema
   m.rates/schema
   m.rate-sources/schema
   m.transactions/schema
   m.users/schema])
