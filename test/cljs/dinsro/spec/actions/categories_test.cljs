(ns dinsro.spec.actions.categories-test
  (:require [devcards.core :as dc :refer-macros [defcard]]
            [dinsro.spec :as ds]
            [dinsro.spec.actions.categories :as s.a.categories]))

(declare create-params-valid)
(defcard create-params-valid
  (ds/gen-key ::s.a.categories/create-params-valid))

(declare create-request-valid)
(defcard create-request-valid
  (ds/gen-key ::s.a.categories/create-request-valid))
