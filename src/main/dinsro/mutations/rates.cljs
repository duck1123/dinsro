(ns dinsro.mutations.rates
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [taoensso.timbre :as timbre]))

(defmutation create! [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete! [_props]
  (action [_env] true)
  (remote [_env] true))
