(ns dinsro.actions.nostr.filter-items
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/filter_items.cljc]]
;; [[../../ui/admin/nostr/filters/filter_items.cljc]]

(s/def ::item-data (s/keys))

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

(>defn determine-item
  "Create a query map based on item's params"
  [item]
  [::m.n.filter-items/item => (? ::item-data)]
  (or
   (when-let [hex (some-> item ::m.n.filter-items/pubkey
                          q.n.pubkeys/read-record ::m.n.pubkeys/hex)]
     {:pubkey hex})
   (when-let [hex (some-> item ::m.n.filter-items/event
                          q.n.events/read-record ::m.n.events/note-id)]
     {:event hex})
   (when-let [kind (some-> item ::m.n.filter-items/kind)]
     {:kind kind})))

(>defn get-query-string
  [item-id]
  [::m.n.filter-items/id => (? ::item-data)]
  (let [item     (q.n.filter-items/read-record item-id)
        hex-maps (determine-item item)]
    (log/trace :get-query-string/mapped {:item item :hex-maps hex-maps})
    hex-maps))
