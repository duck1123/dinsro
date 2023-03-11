(ns dinsro.model.nostr.requests
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs :as ds]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::status string?)
(defattr status ::status :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::relay uuid?)
(defattr relay ::relay :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.relays/id
   ao/schema           :production
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(s/def ::start-time ::ds/date)
(defattr start-time ::start-time :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::end-time ::ds/date)
(defattr end-time ::end-time :date
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::code ::relay]))
(>def ::item (s/keys :req [::id ::code ::relay]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn ident-item [item] [::item => ::ident] (select-keys item [::id]))
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id status relay])
