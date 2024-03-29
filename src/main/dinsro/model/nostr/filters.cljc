(ns dinsro.model.nostr.filters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.requests :as m.n.requests]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::request uuid?)
(defattr request ::request :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.requests/id
   ao/schema           :production
   ::report/column-EQL {::request [::m.n.requests/id ::m.n.requests/code]}})

(>def ::index int?)
(defattr index ::index :int
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::since (? string?))
(defattr since ::since :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::until (? string?))
(defattr until ::until :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::request ::index] :opt [::since ::until]))
(>def ::item (s/keys :req [::id ::request ::index] :opt [::since ::until]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id request since until index])
