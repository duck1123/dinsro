(ns dinsro.specs.events.categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(s/def ::item (s/nilable ::m.categories/item))
(s/def ::items (s/coll-of ::m.categories/item))

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(s/def ::do-delete-record-event (s/cat :id :db/id))

;; Index

(s/def ::do-fetch-index-state ::ds/state)

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event vector?)
(s/def ::do-fetch-index-response (s/keys))

(s/def ::do-fetch-index-failed-cofx (s/keys))
(s/def ::do-fetch-index-failed-event vector?)
(s/def ::do-fetch-index-failed-response (s/keys))
