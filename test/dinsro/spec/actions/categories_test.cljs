(ns dinsro.spec.actions.categories-test
  (:require
   [dinsro.cards :refer-macros [defcard]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.categories :as s.a.categories]))

(defcard create-params-valid
  (ds/gen-key ::s.a.categories/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.categories/create-request-valid))
