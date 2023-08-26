(ns dinsro.model.nostr.filter-items
  (:refer-clojure :exclude [filter type])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

;; [[../../joins/nostr/filter_items.cljc]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::filter uuid?)
(defattr filter ::filter :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.filters/id
   ao/schema           :production
   ::report/column-EQL {::filter [::m.n.filters/id ::m.n.filters/index]}})

;; ids authors kinds #e #p
(>def ::type (? string?))
(defattr type ::type :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::index number?)
(defattr index ::index :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::kind (? number?))
(defattr kind ::kind :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::event (? uuid?))
(defattr event ::event :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.events/id
   ao/schema           :production
   ::report/column-EQL {::event [::m.n.events/id ::m.n.events/address]}})

(>def ::pubkey (? uuid?))
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id ::m.n.pubkeys/hex ::m.n.pubkeys/name]}})

(>def ::params (s/keys :req [::filter ::index]
                       :opt [::kind ::event ::pubkey ::type]))
(>def ::item (s/keys :req [::id ::filter ::index]
                     :opt [::kind ::event ::pubkey  ::type]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id filter index type kind event pubkey])
