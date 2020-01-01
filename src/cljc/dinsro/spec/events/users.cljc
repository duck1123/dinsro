(ns dinsro.spec.events.users
  (:require [clojure.spec.alpha :as s]))

(s/def ::do-fetch-record-failed-cofx (s/keys))
(s/def ::do-fetch-record-failed-event (s/keys))
(s/def ::do-fetch-record-failed-response (s/keys))

(s/def ::do-fetch-record-cofx (s/keys))
(s/def ::do-fetch-record-event (s/keys))
(s/def ::do-fetch-record-response (s/keys))
