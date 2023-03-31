(ns dinsro.queries.nostr.subscriptions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]

(>defn create-record
  [params]
  [::m.n.subscriptions/params => ::m.n.subscriptions/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.subscriptions/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn find-by-relay-and-pubkey
  [relay-id pubkey-id]
  [::m.n.relays/id ::m.n.pubkeys/id => (? ::m.n.subscriptions/id)]
  (log/debug :find-by-relay-and-pubkey/starting {:relay-id relay-id :pubkey-id pubkey-id})
  (c.xtdb/query-id
   '{:find  [?subscription-id]
     :in    [[?relay-id ?pubkey-id]]
     :where [[?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
             [?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]
             [?subscription-id ::m.n.subscriptions/relay ?relay-id]]}
   [relay-id pubkey-id]))

(>defn find-by-pubkey
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.n.subscriptions/id)]
  (log/debug :find-by-pubkey/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-ids
   '{:find  [?subscription-id]
     :in    [[?pubkey-id]]
     :where [[?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
             [?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]]}
   [pubkey-id]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.subscriptions/id)]
  (log/debug :index-ids/starting {})
  (c.xtdb/query-ids '{:find [?id] :where [[?id ::m.n.subscriptions/id _]]}))

(>defn read-record
  [id]
  [::m.n.subscriptions/id => (? ::m.n.subscriptions/item)]
  (log/debug :read-record/starting {:id id})
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.subscriptions/id)
      (let [read-record (dissoc record :xt/id)]
        (log/info :read-record/read {:read-record read-record})
        read-record))))

(>defn find-by-relay-and-code
  [relay-id code]
  [::m.n.relays/id ::m.n.subscriptions/code => (? ::m.n.subscriptions/id)]
  (log/debug :find-by-relay-and-code/starting {:relay-id relay-id :code code})
  (c.xtdb/query-id
   '{:find  [?id]
     :in    [[?relay-id ?code]]
     :where [[?id ::m.n.subscriptions/relay ?relay-id]
             [?id ::m.n.subscriptions/code ?code]]}
   [relay-id code]))

(>defn find-by-relay
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.subscriptions/id)]
  (log/debug :find-by-relay/starting {:relay-id relay-id})
  (c.xtdb/query-ids
   '{:find  [?id]
     :in    [[?relay-id]]
     :where [[?id ::m.n.subscriptions/relay ?relay-id]]}
   [relay-id]))
