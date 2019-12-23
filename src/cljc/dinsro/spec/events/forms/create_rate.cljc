(ns dinsro.spec.events.forms.create-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::rate string?)
(def rate ::rate)

(s/def ::currency-id ds/id-string)
(def currency-id ::currency-id)

(s/def ::date string?)
(def date ::date)

(s/def ::time string?)
(def time ::time)

(s/def ::shown? boolean?)
(def shown? ::shown?)
