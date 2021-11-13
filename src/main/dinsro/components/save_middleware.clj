(ns dinsro.components.save-middleware
  (:require
   [com.fulcrologic.rad.middleware.save-middleware :as r.s.middleware]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [com.fulcrologic.rad.blob :as blob]
   [dinsro.model :as model]))

(def middleware
  (->
   (xt/wrap-xtdb-save)
   (blob/wrap-persist-images model/all-attributes)
   (r.s.middleware/wrap-rewrite-values)))
