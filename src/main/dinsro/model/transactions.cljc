(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::date ::ds/date)
(defattr date ::date :instant
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::date ::description]))
(s/def ::item (s/keys :req [::id ::date ::description]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => ::ident] [::id id])
(defn idents [ids] (mapv (fn [id] {::id id}) ids))

(def attributes [date description id])
