(ns dinsro.model.seed
  (:require
   [dinsro.model.navlink :as m.navlink]))

(defn new-navlink
  [id name href target]
  {:xt/id             id
   ::m.navlink/id     id
   ::m.navlink/name   name
   ::m.navlink/href   href
   ::m.navlink/target target})
