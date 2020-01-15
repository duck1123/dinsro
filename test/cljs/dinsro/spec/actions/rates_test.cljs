(ns dinsro.spec.actions.rates-test
  (:require
   [devcards.core :as dc :refer-macros [defcard]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rates :as s.a.rates]))

(defcard create-params-valid
  (ds/gen-key ::s.a.rates/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rates/create-request-valid))

(defcard create-response-valid
  (ds/gen-key ::s.a.rates/create-response-valid))

(defcard index-by-currency-request
  (ds/gen-key ::s.a.rates/index-by-currency-request))

(defcard index-by-currencies-response
  (ds/gen-key ::s.a.rates/index-by-currency-response))
