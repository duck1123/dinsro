(ns dinsro.cards
  (:require
   [devcards.core]
   [nubank.workspaces.core]
   [nubank.workspaces.card-types.react :as ct.react]
   [reagent.core :as r])
  (:require-macros
   [dinsro.cards]))

(defn reframe-card
  [body]
  (ct.react/react-card
   (r/as-element body)))
