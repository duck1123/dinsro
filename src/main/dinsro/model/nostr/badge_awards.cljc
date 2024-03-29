(ns dinsro.model.nostr.badge-awards
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]))

;; [[../../ui/nostr/badge_awards.cljs][Badge Awards UI]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::badge uuid?)
(defattr badge ::badge :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.badge-definitions/id
   ao/schema           :production
   ::report/column-EQL {::badge [::m.n.badge-definitions/id ::m.n.badge-definitions/code]}})

(>def ::target uuid?)
(defattr target ::target :ref
  {ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.n.pubkeys/id
   ::report/column-EQL {::target [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::required-params (s/keys :req [::badge]))

(>def ::params (s/keys :req [::code ::description ::image-url ::thumbnail-url ::pubkey]))
(>def ::item (s/keys :req [::id ::code ::description ::image-url ::thumbnail-url ::pubkey]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id badge target])
