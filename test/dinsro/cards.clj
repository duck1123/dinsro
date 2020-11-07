(ns dinsro.cards
  (:require
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defmacro defcard-rg
  [name & body]
  `(do
     (require 'dinsro.components.boundary)
     (devcards.core/defcard ~name
       (reagent.core/as-element
        [dinsro.components.boundary/error-boundary
         ((fn [] ~@body))]))))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))
