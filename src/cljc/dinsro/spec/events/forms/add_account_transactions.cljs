(ns dinsro.spec.events.forms.add-account-transactions
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::currency-id ds/id-string)
(def currency-id ::currency-id)

(s/def ::date ds/date-string)
(def date ::date)

(s/def ::value ds/double-string)
(def value ::value)
