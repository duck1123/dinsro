(ns dinsro.ui)

(def strings {})

(defn l
  [keyword]
  (get strings keyword (str "Missing string: " keyword)))
