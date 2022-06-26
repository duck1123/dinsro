(ns dinsro.specs-cards
  (:require
   [dinsro.specs :as ds]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :as viewer :refer [inspect]]))

(dc/defcard site-config [] [inspect "foo"])

(dc/defcard invalid-status [] [inspect (ds/gen-key ::ds/invalid-status)])
