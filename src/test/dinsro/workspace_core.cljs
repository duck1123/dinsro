(ns dinsro.workspace-core
  (:require
   [nubank.workspaces.core :as ws]
   dinsro.all-tests
   [dinsro.client :as client]
   [com.fulcrologic.rad.application :as rad-app]
   [lambdaisland.glogc :as log]))

(defonce app (rad-app/fulcro-rad-app {}))

(defn start!
  []
  (client/install-logging!)
  (log/info :workspace/starting {})
  (client/setup-RAD app)
  (ws/mount))

(defonce init (start!))
