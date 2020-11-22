(ns dinsro.cards
  (:require
   ;; [nubank.workspaces.core]
   ;; [nubank.workspaces.card-types.react :as ct.react]
   [reagent.core :as r])
  (:require-macros
   [dinsro.cards]))

(defmacro defcard-rg
  [name & body]
  `(nubank.workspaces.core/defcard ~name
     (nubank.workspaces.card-types.react/react-card
      (reagent.core/as-element ((fn [] ~@body))))))

(defmacro deftest
  [name & body]
  `(nubank.workspaces.core/deftest ~name ~@body))
