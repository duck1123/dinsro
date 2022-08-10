(ns dinsro.client.converters.currency-unit
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   dinsro.client.converters.get-block-result
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.currency.CurrencyUnit))

(>def ::record
      (s/keys
       :req
       [::value]))

(>defn CurrencyUnit->record
  [this]
  [(ds/instance? CurrencyUnit) => ::record]
  (log/finer :CurrencyUnit->record/starting {:this this})
  (let [record {::value (some-> this .toBigDecimal .toLong)}]
    (log/finer :CurrencyUnit->record/finished {:record record})
    record))

(extend-type CurrencyUnit
  cs/Recordable
  (->record [this] (CurrencyUnit->record this)))
