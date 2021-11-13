(ns dinsro.components.delete-middleware
  (:require
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]))

(def middleware (xt/wrap-xtdb-delete))
