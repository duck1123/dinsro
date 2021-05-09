(ns dinsro.mutations.categories
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [taoensso.timbre :as log]))

(defmutation create! [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete! [_props]
  (action [_env] true)
  (remote [_env] true))
