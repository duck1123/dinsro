(ns dinsro.spec.actions.categories-test
  (:require
   [devcards.core :refer-macros [defcard]]
   [dinsro.cards :as cards]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.categories :as s.a.categories]))

(cards/header
 'dinsro.spec.actions.categories-test
 "Category Action Specs" [])

(defcard create-params-valid
  (ds/gen-key ::s.a.categories/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.categories/create-request-valid))
