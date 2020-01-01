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
            [taoensso.timbre :as timbre]
            #_[conman.core :as conman]
            #_[luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn instrument
  []
  (stest/instrument))

(println "instrumenting")
(instrument)
