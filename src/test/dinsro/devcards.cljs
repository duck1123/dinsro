(ns dinsro.devcards
  (:require
   [devcards.core :as dc :include-macros true]
   [dinsro.core-test]
   [dinsro.ui-test]
   [reagent.core]
   [nubank.workspaces.core :as ws]
   [taoensso.timbre :as timbre]))

(dc/defcard-rg foo
  [:p "Foo"])

(defn main
  []
  (timbre/info "Starting devcards")
  (dc/start-devcard-ui!))

(defonce init (ws/mount))
