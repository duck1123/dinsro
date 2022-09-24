(ns dinsro.workspace-core
  (:require
   [com.fulcrologic.rad.application :as rad-app]
   dinsro.all-tests
   [dinsro.client :as client]
   [lambdaisland.glogc :as log]
   [lambdaisland.glogi.console :as glogi-console]
   [nubank.workspaces.core :as ws]))

(defonce app (rad-app/fulcro-rad-app {}))

(defn start!
  []
  (glogi-console/install!)
  (log/info :workspace/starting {})
  (client/setup-RAD app)
  (ws/mount))

(defonce init (start!))
