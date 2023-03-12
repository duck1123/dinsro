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

(>def ::fetch!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))
