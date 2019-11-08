(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require [dinsro.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [dinsro.figwheel :refer [start-fw stop-fw cljs]]
            [dinsro.core :refer [start-app]]
            [dinsro.db.core]
            [orchestra.spec.test :as stest]
            [taoensso.timbre :as timbre]))

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

(defn instrument
  []
  (stest/instrument))

(instrument)
