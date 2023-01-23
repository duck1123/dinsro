(ns dinsro.model.nostr.subscription-pubkeys
  "Model describing the pubkeys that have been requested as part of a subscription"
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

;; [[subscriptions.cljc][Subscriptions Model]]
(>def ::subscription uuid?)
(defattr subscription ::subscription :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.subscriptions/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.subscriptions/id ::m.n.subscriptions/code]}})

(s/def ::required-params (s/keys :req [::code ::relay]))
(s/def ::params (s/keys :req [::code ::relay]))
(s/def ::item (s/keys :req [::id ::code ::relay]))
(s/def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn ident-item [item] [::item => ::ident] (select-keys item [::id]))
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id code subscription])

#?(:clj (def resolvers []))
