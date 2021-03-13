(ns dinsro.devcards
  (:require
   [dinsro.core-test]
   [dinsro.specs-test]
   [dinsro.views-test]
   [dinsro.ui-test]
   [reagent.core]
   [nubank.workspaces.core :as ws]
   [taoensso.timbre :as timbre]))

(defonce init (ws/mount))
