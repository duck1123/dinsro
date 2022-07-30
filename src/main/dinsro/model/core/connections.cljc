(ns dinsro.model.core.connections
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::port int?)
(defattr port ::port :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcuser string?)
(defattr rpcuser ::rpcuser :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcpass string?)
(defattr rpcpass ::rpcpass :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]))

(s/def ::params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(s/def ::item
  (s/keys :req [::id ::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(s/def ::items (s/coll-of ::item))

(def link-query [::id ::name])

(defn ident
  [id]
  {::id id})

(defn ident-item
  [{::keys [id]}]
  (ident id))

(defn idents
  [ids]
  (mapv ident ids))

(def attributes
  [id name host port rpcuser rpcpass])
