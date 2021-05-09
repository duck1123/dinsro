(ns dinsro.components.datahike
  (:require
   [clojure.java.io :as io]
   [datahike.api :as d]
   [datahike.config :as d.config]
   [datahike.db :as db]
   [dinsro.components.config :as config]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log])
  (:import java.io.File))

(def db-error-message
  "Database configuration not found, :datahike-url environment variable must be set before running")

(defn get-url
  []
  (or (config/config :datahike-url)
      (when-let [data-path (config/config :data-path)]
        (let [file (File. data-path)]
          (when (.exists file)
            (str "datahike:file://" (.getAbsolutePath file)))))))

(defn initialize-db!
  [uri]
  (when-not (d/database-exists? (d.config/uri->config uri))
    (log/info "Creating database: " uri)
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
  :stop (do (log/info "stopping real connection")
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
