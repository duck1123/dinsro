(ns dinsro.spec.actions.rate-sources-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rate-sources :as s.a.rate-sources]))

(cards/header "Rate Source Actions Specs" [])

(defcard create-params-valid
  (ds/gen-key ::s.a.rate-sources/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rate-sources/create-request-valid))
