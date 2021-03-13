(ns dinsro.core
  (:require
   [taoensso.timbre :as timbre]))

(defn init!
  [debug?]
  (timbre/infof "init - %s" debug?))
