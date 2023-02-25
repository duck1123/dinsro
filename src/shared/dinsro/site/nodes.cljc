(ns dinsro.site.nodes
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::bitcoin boolean?)
(s/def ::lnd boolean?)
(s/def ::fileserver boolean?)
(s/def ::rtl boolean?)
(s/def ::specter boolean?)
(s/def ::lnbits boolean?)

(s/def ::item
  (s/keys
   :req-un
   [::bitcoin
    ::lnd
    ::fileserver
    ::rtl
    ::specter
    ::lnbits]))
(s/def ::nodes (s/map-of string? ::item))
