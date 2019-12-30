(ns dinsro.spec.events.forms.create-account
  (:require [clojure.spec.alpha :as s]))

(def default-name "Offshore")
(def default-initial-value 1.0)

(s/def ::name string?)
(s/def ::currency-id string?)
(s/def ::user-id string?)
(s/def ::shown? boolean?)
(s/def ::initial-value string?)
