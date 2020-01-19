(ns dinsro.spec.actions.rates-test
  (:require
   [devcards.core :as dc :refer-macros [defcard]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rates :as s.a.rates]))

(defcard create-params-valid
  (ds/gen-key ::s.a.rates/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rates/create-request-valid))
