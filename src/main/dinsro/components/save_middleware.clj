(ns dinsro.components.save-middleware
  (:require
   [com.fulcrologic.rad.blob :as blob]
   [com.fulcrologic.rad.middleware.save-middleware :as r.s.middleware]
   [dinsro.model :as model]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]))

(def middleware
  (->
   (xt/wrap-xtdb-save)
   (blob/wrap-persist-images model/all-attributes)
   (r.s.middleware/wrap-rewrite-values)))
