(ns dinsro.specs.events.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.rates :as m.rates]))

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::m.rates/item))

(s/def ::do-delete-record-success-response (s/keys))


(s/def ::add-record-event-response (s/keys))
(s/def ::add-record-event-without-response (s/cat :id :db/id))
(s/def ::add-record-event-with-response (s/cat :id :db/id :response ::add-record-event-response))
(s/def ::add-record-event (s/cat :id :db/id :response (s/? ::add-record-event-response)))
(s/def ::add-record-cofx (s/keys))
