(ns dinsro.specs.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]))

(>def ::fetch!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

(>def ::connect!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::connect!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::connect!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::connect!-response
  (s/or :success ::connect!-response-success
        :error ::connect!-response-error))
