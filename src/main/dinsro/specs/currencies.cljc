(ns dinsro.specs.currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]))

(s/def ::deleted-records (s/coll-of ::m.currencies/id))
(s/def ::delete!-request (s/keys :req [::m.currencies/id]))
(s/def ::delete!-response (s/keys :opt [::mu/errors ::mu/status ::deleted-records]))
