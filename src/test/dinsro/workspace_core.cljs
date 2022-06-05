(ns dinsro.workspace-core
  (:require
   [nubank.workspaces.core :as ws]
   dinsro.all-tests
   [dinsro.client :as client]
   [lambdaisland.glogi.console :as glogi-console]
   [taoensso.timbre :as timbre]
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [com.fulcrologic.rad.application :as rad-app]
   [lambdaisland.glogc :as log]))

(defonce app (rad-app/fulcro-rad-app {}))

(defn start!
  []
  (glogi-console/install!)
  (timbre/merge-config! {:level     :debug
                         :min-level :debug
                         :output-fn prefix-output-fn
                         :appenders {:console (console-appender)}})
  (log/info :workspace/starting {})
  (client/setup-RAD app)
  (ws/mount))

(defonce init (start!))
