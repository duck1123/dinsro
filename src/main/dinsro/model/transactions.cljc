(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.specs :as ds]))

;; [[../joins/transactions.cljc]]
;; [[../ui/admin/transactions.cljs]]
;; [[../ui/transactions.cljs]]

(>def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::date ::ds/date)
(defattr date ::date :instant
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::date ::description]))
(>def ::item (s/keys :req [::id ::date ::description]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id]
  [::id => ::ident] [::id id])
(>defn idents [ids]
  [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv (fn [id] {::id id}) ids))

(def attributes [date description id])
