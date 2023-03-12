(ns dinsro.model.nostr.subscription-pubkeys
  "Model describing the pubkeys that have been requested as part of a subscription"
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]))

;; [[../../actions/nostr/subscription_pubkeys.clj][Subscription Pubkey Actions]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../queries/nostr/subscriptions.clj][Subscription Queries]]
;; [[./subscriptions.cljc][Subscriptions Model]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::subscription uuid?)
(defattr subscription ::subscription :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.subscriptions/id
   ao/schema           :production
   ::report/column-EQL {::subscription [::m.n.subscriptions/id ::m.n.subscriptions/code]}})

(>def ::pubkey uuid?)
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::required-params (s/keys :req [::subscription ::pubkey]))
(>def ::params (s/keys :req [::subscription ::pubkey]))
(>def ::item (s/keys :req [::id ::subscription ::pubkey]))
(>def ::items (s/coll-of ::item))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id pubkey subscription])
