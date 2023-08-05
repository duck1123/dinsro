(ns dinsro.specs.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj  [com.fulcrologic.guardrails.core :refer [>def =>]]
      :cljs [com.fulcrologic.guardrails.core :refer [>def =>]])
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]))

;; Add Contact

(>def ::add-contact!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::add-contact!-response-success
  (s/keys :req [::mu/status]))

(>def ::add-contact!-response-error
  (s/keys :req [::mu/status]))

(>def ::add-contact!-response
  (s/or :success ::add-contact!-response-success
        :error ::add-contact!-response-error))

;; fetch!

(>def ::fetch!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

;; fetch-contacts!

(>def ::fetch-contacts!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch-contacts!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch-contacts!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch-contacts!-response
  (s/or :success ::fetch-contacts!-response-success
        :error ::fetch-contacts!-response-error))

;; fetch-events!

(>def ::fetch-events!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch-events!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch-events!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch-events!-response
  (s/or :success ::fetch-events!-response-success
        :error ::fetch-events!-response-error))
