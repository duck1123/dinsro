(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [taoensso.timbre :as timbre]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))
