(ns dinsro.model.users-cards
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :as viewer :refer [inspect]]))

(dc/defcard item [] [inspect (ds/gen-key ::m.users/item)])
