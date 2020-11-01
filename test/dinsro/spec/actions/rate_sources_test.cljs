(ns dinsro.spec.actions.rate-sources-test
  (:require
   [dinsro.cards :refer-macros [defcard]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rate-sources :as s.a.rate-sources]))

(defcard create-params-valid
  (ds/gen-key ::s.a.rate-sources/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rate-sources/create-request-valid))
