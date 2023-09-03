(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> >def >defn]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

;; [[../options/accounts.cljc]]
;; [[../ui/accounts.cljc]]

(comment ::auth/_ ::pc/_)

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::initial-value (s/or :double double?
                             :zero zero?
                             :number number?))

(defattr initial-value ::initial-value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/cardinality      :one
   ao/required?        true
   ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.currencies/id
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(s/def ::source (s/or :id ::m.rate-sources/id
                      :nil nil?))
(defattr source ::source :ref
  {ao/cardinality :one
   ao/required?   true
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.rate-sources/id
   ::report/column-EQL {::source [::m.rate-sources/id ::m.rate-sources/name]}})

(s/def ::wallet (s/or :id ::m.c.wallets/id
                      :nil nil?))
(defattr wallet ::wallet :ref
  {ao/cardinality :one
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.c.wallets/id
   ::report/column-EQL {::wallet [::m.c.wallets/id ::m.c.wallets/name]}})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/cardinality      :one
   ao/required?        true
   ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.users/id
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(s/def ::required-params
  (s/keys :req [::name
                ::initial-value]))

(def required-params
  "Required params for accounts"
  ::required-params)
(s/def ::params (s/keys :req [::currency ::initial-value ::name ::user]
                        :opt [::source ::wallet]))
(s/def ::item (s/keys :req [::id ::currency ::initial-value ::name ::user]
                      :opt [::source ::wallet]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [currency id initial-value name source user wallet])
