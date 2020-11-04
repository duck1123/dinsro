(ns dinsro.cards
  (:require
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defmacro defcard-rg
  [name & body]
  `(devcards.core/defcard ~name
     (reagent.core/as-element [error-boundary ((fn [] ~@body))])))

(defmacro defcard
  [name & body]
  `(devcards.core/defcard ~name ~@body))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))
