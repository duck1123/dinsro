(ns dinsro.cards)

(defmacro defcard-rg
  [name & body]
  `(nubank.workspaces.core/defcard ~name
     (nubank.workspaces.card-types.react/react-card
      (reagent.core/as-element ((fn [] ~@body))))))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))
