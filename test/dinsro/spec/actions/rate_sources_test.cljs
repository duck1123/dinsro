(ns dinsro.spec.actions.rate-sources-test
  (:require
   [devcards.core :refer-macros [defcard]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rate-sources :as s.a.rate-sources]))

(cards/header
 'dinsro.spec.actions.rate-sources-test
 "Rate Source Actions Specs" [])

(defcard create-params-valid
  (ds/gen-key ::s.a.rate-sources/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rate-sources/create-request-valid))
