(ns dinsro.cards
  #?(:cljs
     (:require
      [nubank.workspaces.core]
      [nubank.workspaces.card-types.react :as ct.react]
      [reagent.core :as r]))
  #?(:cljs
     (:require-macros
      [dinsro.cards])))

#?(:cljs
   (defn reframe-card
     [body]
     (ct.react/react-card
      (r/as-element body))))

(defmacro defcard-rg
  [name & body]
  `(nubank.workspaces.core/defcard ~name
     (nubank.workspaces.card-types.react/react-card
      (reagent.core/as-element ((fn [] ~@body))))))

(defmacro deftest
  [name & body]
  `(nubank.workspaces.core/deftest ~name ~@body))

(defmacro defcard
  [name & body]
  `(comment ~name ~@body))
