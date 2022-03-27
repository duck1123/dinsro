(ns dinsro.model.core.words
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallets :as m.c.wallets]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::wallet uuid?)
(defattr wallet ::wallet :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.wallets/id
   ao/schema           :production
   ;; ::report/column-EQL {::node [::m.core-nodes/id ::m.core-nodes/name]}
   })

(s/def ::word string?)
(defattr word ::word :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::position number?)
(defattr position ::position :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::word ::position]))
(s/def ::params  (s/keys :req [::word ::position]))
(s/def ::item (s/keys :req [::id ::word ::position]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))
(s/def ::ident-map (s/keys :req [::id]))

(>defn ident
  [id]
  [::id => ::ident-map]
  {::id id})

(>defn idents
  [ids]
  [(s/coll-of ::id) => (s/coll-of ::ident-map)]
  (mapv ident ids))

(def attributes [id word position wallet])
