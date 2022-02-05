(ns dinsro.components.auto-resolvers
  (:require
   [dinsro.model :refer [all-attributes]]
   [mount.core :refer [defstate]]
   [com.fulcrologic.rad.resolvers :as res]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]))

(defstate automatic-resolvers
  :start
  (vec
   (concat
    (res/generate-resolvers all-attributes)
    (xt/generate-resolvers all-attributes :production))))
