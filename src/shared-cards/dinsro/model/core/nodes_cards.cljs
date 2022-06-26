(ns dinsro.model.core.nodes-cards
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.specs :as ds]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :as viewer :refer [inspect]]))

(dc/defcard item [] [inspect (ds/gen-key ::m.c.nodes/item)])
