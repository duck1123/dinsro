(ns dinsro.queries.nostr.subscriptions
  (:require
   ;; [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

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
  (log/info :find/starting {:relay-id relay-id :pubkey-id pubkey-id})
  (let [db      (c.xtdb/main-db)
        query   '{:find  [?subscription-id]
                  :in    [[?relay-id ?pubkey-id]]
                  :where [[?relay-id ::m.n.relays/address ?address]]}
        results (xt/q db query [relay-id pubkey-id])
        id      (ffirst results)]
    (log/info :find/finished {:id id :results results})
    id))
