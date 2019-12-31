(ns dinsro.spec.events.forms.add-account-transaction
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::currency-id ::ds/id-string)
(def currency-id ::currency-id)

(s/def ::date ::ds/date-string)
(def date ::date)

(s/def ::value ::ds/double-string)
(def value ::value)
