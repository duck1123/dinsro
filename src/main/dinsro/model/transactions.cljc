(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::account ::m.accounts/id)
(defattr account ::account :ref
  {ao/identities #{::id}
   ao/schema     :production
   ao/target     ::m.accounts/id})

(s/def ::date ::ds/date)
(defattr date ::date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value ::ds/valid-double)
(defattr value ::value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::date ::description ::value]))
(s/def ::params (s/keys :req [::account ::date ::description ::value]))
(s/def ::item (s/keys :req [::id ::account ::date ::description ::value]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(defattr all-transactions ::all-transactions :ref
  {ao/target    ::id
   ao/pc-output [{::all-transactions [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-transactions (queries/get-all-transactions env query-params)}
        :cljs
        (comment env query-params)))})

(def attributes [account all-transactions date description id value])

#?(:clj (def resolvers []))
