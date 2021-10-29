(ns dinsro.model.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.currencies :as m.currencies]
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

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/identities #{::id}
   ao/schema     :production
   ao/target     ::m.currencies/id})

(s/def ::date ds/date)
(s/def ::date-ms pos-int?)
(s/def ::date-inst inst?)

(defattr date ::date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::source ::m.rate-sources/id)
(defattr source ::source :ref
  {ao/cardinality :one
   ao/identities #{::id}
   ao/schema     :production
   ao/target     ::m.rate-sources/id})

(s/def ::required-params (s/keys :req [::rate ::date]))
(s/def ::params (s/keys :req [::rate ::currency ::date ::source]))
(s/def ::item (s/keys :req [::id ::rate ::currency ::date ::source]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(defattr all-rates ::all-rates :ref
  {ao/target    ::id
   ao/pc-output [{::all-rates [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-rates (queries/get-all-rates env query-params)}
        :cljs
        (comment env query-params)))})

(defattr link ::link :ref
  {ao/cardinality :one
   ao/target      ::id
   ao/pc-input    #{::id}
   ao/pc-output   [{::link [::id]}]
   ao/pc-resolve  (fn [_env params] {::link params})})

(s/def ::rate-feed-item (s/cat :date ::date-ms
                               :rate ::rate))
(s/def ::rate-feed (s/coll-of ::rate-feed-item))

(def attributes [all-rates currency date id link rate source])

#?(:clj (def resolvers []))
