(ns dinsro.model.user-pubkeys
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.currencies :as m.currencies]
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

(s/def ::user-id ::m.users/id)
(defattr user-id ::user-id :ref
  {ao/target           ::m.currencies/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(s/def ::pubkey-id ::m.users/id)
(defattr pubkey-id ::user-id :ref
  {ao/target           ::m.currencies/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(s/def ::required-params (s/keys :req [::name ::url ::active? ::path ::identity?]))
(s/def ::params (s/keys :req [::name ::url ::currency ::active? ::path ::identity?]))
(s/def ::item (s/keys :req [::id ::name ::url ::currency ::active? ::path ::identity?]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id user-id pubkey-id])
