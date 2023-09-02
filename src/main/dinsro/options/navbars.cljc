(ns dinsro.options.navbars
  (:require [dinsro.model.navbars :as m.navbars]))

(def id
  "The id of a navbar"
  ::m.navbars/id)

(def children
  "The links that belong to this navbar in order"
  ::m.navbars/children)
