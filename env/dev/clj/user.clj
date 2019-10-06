(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require [dinsro.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [dinsro.figwheel :refer [start-fw stop-fw cljs]]
            [dinsro.core :refer [start-app]]
            [dinsro.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'dinsro.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'dinsro.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'dinsro.db.core/*db*)
  (mount/start #'dinsro.db.core/*db*)
  (binding [*ns* 'dinsro.db.core]
    (conman/bind-connection dinsro.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
