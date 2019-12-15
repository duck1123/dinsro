(ns dinsro.spec.actions.categories
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.categories :as s.categories]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def :create-category-valid/params
  (s/keys :req-un [
                   ::s.categories/name
                   ;; ::s.accounts/initial-value
                   ::s.categories/user-id
                   ;; ::s.accounts/currency-id
                   ]))
(s/def :create-category/params
  (s/keys :opt-un [
                   ::s.categories/name
                   ;; ::s.accounts/initial-value
                   ::s.categories/user-id
                   ;; ::s.accounts/currency-id
                   ]))
