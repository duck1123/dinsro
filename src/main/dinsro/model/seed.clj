(ns dinsro.model.seed
  (:require
   [dinsro.model.navlink :as m.navlink]))

(defn new-navlink
  [id name href]
  {:crux.db/id      id
   ::m.navlink/id   id
   ::m.navlink/name name
   ::m.navlink/href href})
