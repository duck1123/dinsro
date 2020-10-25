(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [orchestra.spec.test :as stest]
   [taoensso.timbre :as timbre]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn instrument
  []
  (stest/instrument))

(instrument)
