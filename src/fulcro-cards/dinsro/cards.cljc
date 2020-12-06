(ns dinsro.cards
  #?(:cljs
     (:require
      [reagent.core :as r]))
  #?(:cljs
     (:require-macros
      [dinsro.cards])))

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

(defmacro assert-spec
  [_spec _value]
  `(comment))
