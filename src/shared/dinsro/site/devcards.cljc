(ns dinsro.site.devcards
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::enabled boolean?)
(s/def ::embedded boolean?)
(s/def ::inheritHost boolean?)
(s/def ::host string?)

(s/def ::devcards
  (s/keys
   :req-un
   [::enabled
    ::embedded
    ::inheritHost
    ::host]))
