(ns dinsro.spec.events.accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [taoensso.timbre :as timbre]))

(s/def ::item (s/nilable ::s.accounts/item))
(s/def ::items (s/coll-of ::s.accounts/item))

;; Index

(s/def ::do-fetch-index-state keyword?)

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event vector?)
(s/def ::do-fetch-index-response (s/keys))

(s/def ::do-fetch-index-failed-cofx (s/keys))
(s/def ::do-fetch-index-failed-event vector?)
(s/def ::do-fetch-index-failed-response (s/keys))
