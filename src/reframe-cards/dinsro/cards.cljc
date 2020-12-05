(ns dinsro.cards
  #?(:cljs (:require [devcards.core]
                     [reagent.core :as r])))

(defmacro defcard-rg
  [name & body]
  `(devcards.core/defcard ~name
     (reagent.core/as-element ((fn [] ~@body)))))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))
