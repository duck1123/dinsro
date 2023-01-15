(ns dinsro.model.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs :as ds]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::rate ds/valid-double)
(defattr rate ::rate :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::date ds/date)
(s/def ::date-ms pos-int?)

(defattr date ::date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::source ::m.rate-sources/id)
(defattr source ::source :ref
  {ao/cardinality :one
   ao/identities #{::id}
   ao/schema     :production
   ao/target     ::m.rate-sources/id
   ::report/column-EQL {::source [::m.rate-sources/id ::m.rate-sources/name]}})

(s/def ::params (s/keys :req [::rate ::date ::source]))
(s/def ::item (s/keys :req [::id ::rate ::date ::source]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => any?]
  {::id id})

(>defn idents
  [ids]
  [(s/coll-of ::id) => any?]
  (mapv ident ids))

(s/def ::rate-feed-item (s/cat :date ::date-ms
                               :rate ::rate))
(s/def ::rate-feed (s/coll-of ::rate-feed-item))

(def attributes [date id rate source])
