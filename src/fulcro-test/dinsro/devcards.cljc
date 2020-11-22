(ns dinsro.devcards
  (:require
   [devcards.core :as dc :include-macros true]
   [reagent.core]
   [taoensso.timbre :as timbre]))

(defn main
  []
  (timbre/info "Starting devcards")
  (dc/start-devcard-ui!))
