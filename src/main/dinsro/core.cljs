(ns dinsro.core
  (:require
   [taoensso.timbre :as log]))

(defn init!
  [debug?]
  (log/infof "init - %s" debug?))
