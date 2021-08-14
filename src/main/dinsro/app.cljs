(ns dinsro.app
  (:require
   [com.fulcrologic.rad.application :as rad-app]
   [taoensso.timbre :as log]))

(defonce app (rad-app/fulcro-rad-app {}))
