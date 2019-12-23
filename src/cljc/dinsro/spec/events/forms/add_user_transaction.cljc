(ns dinsro.spec.events.forms.add-user-transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::currency-id string?)
(def currency-id ::currency-id)

(s/def ::date string?)
(def date ::date)

(s/def ::value string?)
(def value ::value)

(s/def ::form-data-input
  (s/cat :value ::value))
(s/def ::form-data-output :create-transactions-request-valid/params)

(comment
  (ds/gen-key ::form-data-input)
  (ds/gen-key ::form-data-output)
  )
