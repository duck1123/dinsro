(ns dinsro.db.core
  (:require [camel-snake-kebab.extras :refer [transform-keys]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clj-time.jdbc]
            [conman.core :as conman]
            [mount.core :refer [defstate]]
            [dinsro.config :refer [env]]))

(defstate ^:dynamic *db*
          :start (conman/connect! {:jdbc-url (env :database-url)})
          :stop (conman/disconnect! *db*))

(defn result-one-snake->kebab
  [this result options]
  (transform-keys
   ->kebab-case-keyword
   (hugsql.adapter/result-one this result options)))

(defn result-many-snake->kebab
  [this result options]
  (map #(transform-keys ->kebab-case-keyword %)
       (hugsql.adapter/result-many this result options)))

(defmethod hugsql.core/hugsql-result-fn :1 [sym]
  'dinsro.db.core/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :one [sym]
  'dinsro.db.core/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :* [sym]
  'dinsro.db.core/result-many-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :many [sym]
  'dinsro.db.core/result-many-snake->kebab)

(conman/bind-connection *db* "sql/queries.sql")
