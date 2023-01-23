(ns dinsro.model.nostr.subscriptions
  "Model describing an event subscription to a relay"
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

;; [[relays.cljc][Relays Model]]
(>def ::relay uuid?)
(defattr relay ::relay :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.relays/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.relays/id ::m.n.relays/address]}})

(>def ::params (s/keys :req [::code ::relay]))
(>def ::item (s/keys :req [::id ::code ::relay]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id code relay])
