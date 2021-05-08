(ns dinsro.components.auto-resolvers
  (:require
   [dinsro.model :refer [all-attributes]]
   [mount.core :refer [defstate]]
   [com.fulcrologic.rad.resolvers :as res]
   [roterski.fulcro.rad.database-adapters.crux :as crux]
   [taoensso.timbre :as timbre]))

(defstate automatic-resolvers
  :start
  (vec
   (concat
    (res/generate-resolvers all-attributes)
    (crux/generate-resolvers all-attributes :production))))
