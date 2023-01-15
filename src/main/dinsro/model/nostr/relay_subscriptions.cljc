(ns dinsro.model.nostr.relay-subscriptions
  (:require
   ;; [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

;; [[relays.cljc][Relays Model]]
(>def ::relay uuid?)
(defattr relay ::relay :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.relays/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.relays/id ::m.n.relays/address]}})

;; [[pubkeys.cljc][Pubkeys Model]]
(>def ::pubkey uuid?)
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.pubkeys/id ::m.n.pubkeys/pubkey]}})

(>def ::params (s/keys :req [::relay ::pubkey]))
(>def ::item (s/keys :req [::id ::relay ::pubkey]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id relay pubkey])
