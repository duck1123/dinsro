(ns dinsro.notebook-utils
  (:require
   [nextjournal.clerk.viewer :as v]))

(defn display
  [o]
  (v/html [:pre [:code o]]))
