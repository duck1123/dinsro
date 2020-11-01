(ns dinsro.specs.actions.admin-transactions
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::create-params (s/keys))
(s/def ::create-request (s/keys))
(s/def ::create-response (s/keys))

(s/def ::read-request (s/keys))
(s/def ::read-response (s/keys))

(s/def ::index-request (s/keys))
(s/def ::index-response (s/keys))

(s/def ::delete-request (s/keys))
(s/def ::delete-response (s/keys))
