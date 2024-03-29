(ns dinsro.model.core.connections
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::port int?)
(defattr port ::port :int
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::rpcuser string?)
(defattr rpcuser ::rpcuser :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::rpcpass string?)
(defattr rpcpass ::rpcpass :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]))

(>def ::params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(>def ::item
  (s/keys :req [::id ::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(>def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id name host port rpcuser rpcpass])
