(ns dinsro.actions.nostr.filter-items
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]))

(>defn register-pubkey!
  "Create a filter item for a pubkey"
  ([filter-id pubkey-id]
   [::m.n.filters/id ::m.n.pubkeys/id => ::m.n.filter-items/id]
   (register-pubkey! filter-id pubkey-id 0))
  ([filter-id pubkey-id index]
   [::m.n.filters/id ::m.n.pubkeys/id ::m.n.filter-items/index => ::m.n.filter-items/id]
   (q.n.filter-items/create-record
    {::m.n.filter-items/pubkey pubkey-id
     ::m.n.filter-items/filter filter-id
     ::m.n.filter-items/index  index})))

(defn register-kind!
  ([filter-id kind]
   (register-kind! filter-id kind 0))
  ([filter-id kind index]
   (q.n.filter-items/create-record
    {::m.n.filter-items/kind   kind
     ::m.n.filter-items/filter filter-id
     ::m.n.filter-items/index  index})))
