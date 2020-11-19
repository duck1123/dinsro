(ns dinsro.devcards
  (:require
   [devcards.core :as dc :include-macros true]
   [dinsro.events-test]
   [dinsro.ui-test]
   [dinsro.views-test]
   [reagent.core]
   [taoensso.timbre :as timbre]))

(dc/defcard-rg foo
  [:p "Foo"])

(defn main
  []
  (timbre/info "Starting devcards")
  (dc/start-devcard-ui!))
