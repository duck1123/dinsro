(ns dinsro.components.auto-resolvers
  (:require
   [com.fulcrologic.rad.resolvers :as res]
   [dinsro.model :refer [all-attributes]]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]))

(defstate automatic-resolvers
  :start
  (vec
   (concat
    (res/generate-resolvers all-attributes)
    (xt/generate-resolvers all-attributes :production))))
