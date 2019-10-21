(ns dinsro.components.index-rates)

(defn index-rates
  [items]
  (if-not (seq items)
    [:p "No Rates"]
    [:p "Rates"]
    )
  )
