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
  (log/info :create-record/starting {:params params})
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
  (log/info :find-by-relay-and-pubkey/starting {:relay-id relay-id :pubkey-id pubkey-id})
  (let [db      (c.xtdb/main-db)
        query   '{:find  [?subscription-id]
                  :in    [[?relay-id ?pubkey-id]]
                  :where [[?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
                          [?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]
                          [?subscription-id ::m.n.subscriptions/relay ?relay-id]]}
        results (xt/q db query [relay-id pubkey-id])
        id      (ffirst results)]
    (log/info :find-by-relay-and-pubkey/finished {:id id :results results})
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.subscriptions/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find [?id] :where [[?id ::m.n.subscriptions/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn read-record
  [id]
  [::m.n.subscriptions/id => (? ::m.n.subscriptions/item)]
  (log/info :read-record/starting {:id id})
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.subscriptions/id)
      (let [read-record (dissoc record :xt/id)]
        (log/info :read-record/read {:read-record read-record})
        read-record))))

(>defn find-by-relay-and-code
  [relay-id code]
  [::m.n.relays/id ::m.n.subscriptions/code => (? ::m.n.subscriptions/id)]
  (log/info :find-by-relay-and-code/starting {:relay-id relay-id :code code})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?relay-id ?code]]
                :where [[?id ::m.n.subscriptions/relay ?relay-id]
                        [?id ::m.n.subscriptions/code ?code]]}
        id    (ffirst (xt/q db query [relay-id code]))]
    (log/info :find-by-relay-and-code/finished {:id id})
    id))
