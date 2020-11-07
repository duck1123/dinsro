(ns dinsro.specs.events.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs.rate-sources :as s.rate-sources]))

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::s.rate-sources/item))
