(ns dinsro.specs.events.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.rate-sources :as m.rate-sources]))

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::m.rate-sources/item))
