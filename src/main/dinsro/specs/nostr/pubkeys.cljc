(ns dinsro.specs.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   ;; #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   ;; [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:clj  [com.fulcrologic.guardrails.core :refer [>def
                                                     ;; >defn
                                                     =>]]
      :cljs [com.fulcrologic.guardrails.core :refer [>def =>]])
   ;; #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   ;; [com.wsscode.pathom.connect :as pc]
   ;; #?(:clj [dinsro.actions.nostr.badge-definitions :as a.n.badge-definitions])
   ;; #?(:clj [dinsro.actions.nostr.events :as a.n.events])
   ;; #?(:clj [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts])
   ;; #?(:clj [dinsro.actions.nostr.pubkey-events :as a.n.pubkey-events])
   ;; #?(:clj [dinsro.actions.nostr.pubkeys :as a.n.pubkeys])
   ;; #?(:clj [dinsro.actions.nostr.subscription-pubkeys :as a.n.subscription-pubkeys])
   ;; [dinsro.model.contacts :as m.contacts]
   ;; [dinsro.model.core.nodes :as m.c.nodes]
   ;; [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   ;; [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   ;; [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   ;; [lambdaisland.glogc :as log]
   ))

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
