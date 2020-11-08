(ns dinsro.cards)

(defmacro defcard-rg
  [name & body]
  `(do
     (require 'dinsro.ui.boundary)
     (devcards.core/defcard ~name
       (reagent.core/as-element
        [dinsro.ui.boundary/error-boundary
         ((fn [] ~@body))]))))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))
