(ns dinsro.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [taoensso.timbre :as log]))

(defmutation submit [props]
  (action [_env]
    (log/infof "submitting: %s" props)))
