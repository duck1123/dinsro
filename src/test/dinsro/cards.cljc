(ns dinsro.cards
  #?(:cljs
     (:require
      [nubank.workspaces.core]))
  #?(:cljs
     (:require-macros
      [dinsro.cards])))

(defmacro deftest
  [name & body]
  `(nubank.workspaces.core/deftest ~name ~@body))

(defmacro defcard
  [name & body]
  `(comment ~name ~@body))

(defmacro assert-spec
  [_spec _value]
  `(comment))
