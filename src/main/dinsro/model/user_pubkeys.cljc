(ns dinsro.model.user-pubkeys
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> >def >defn]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

;; [[../actions/users.clj][Actions]]
;; [[../queries/users.clj][Queries]]
;; [[../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../model/users.cljc][Users Model]]

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::user ::m.users/ident)
(defattr user ::user :ref
  {ao/target           ::m.users/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(s/def ::pubkey ::m.n.pubkeys/ident)
(defattr pubkey ::pubkey :ref
  {ao/target           ::m.n.pubkeys/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id ::m.n.pubkeys/name]}})

(s/def ::params (s/keys :req [::name ::url ::currency ::active? ::path ::identity?]))
(s/def ::item (s/keys :req [::id ::name ::url ::currency ::active? ::path ::identity?]))
(s/def ::items (s/coll-of ::item))
(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id user pubkey])
