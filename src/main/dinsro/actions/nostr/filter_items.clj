(ns dinsro.actions.nostr.filter-items
  (:require
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]))

(defn register-pubkey!
  [filter-id pubkey-id]
  (q.n.filter-items/create-record
   {::m.n.filter-items/pubkey pubkey-id
    ::m.n.filter-items/filter filter-id}))
