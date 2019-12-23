(ns dinsro.spec.events.forms.create-transaction
  (:require [clojure.spec.alpha :as s]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id string?)
(s/def ::date string?)
(s/def ::shown? boolean?)
(s/def ::value string?)
