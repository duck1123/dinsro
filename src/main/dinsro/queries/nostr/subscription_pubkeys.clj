(ns dinsro.queries.nostr.subscription-pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]
;; [[../../model/nostr/subscriptions.cljc][Subscriptions Model]]


(>defn create-record
  [params]
  [::m.n.subscription-pubkeys/params => ::m.n.subscription-pubkeys/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.subscription-pubkeys/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.subscription-pubkeys/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find [?id] :where [[?id ::m.n.subscription-pubkeys/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn read-record
  [id]
  [::m.n.subscription-pubkeys/id => (? ::m.n.subscription-pubkeys/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/info :read-record/starting {:record record})
    (when (get record ::m.n.subscription-pubkeys/id)
      (dissoc record :xt/id))))

(>defn index-by-relay
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.subscription-pubkeys/id)]
  (log/info :index-by-relay/starting {:relay-id relay-id})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?relay-id]]
                :where [[?id ::m.n.subscription-pubkeys/relay ?relay-id]]}
        ids   (map first (xt/q db query [relay-id]))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn delete!
  [id]
  [::m.n.subscription-pubkeys/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all!
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn find-by-subscription-and-pubkey
  [subscription-id pubkey-id]
  [::m.n.subscription-pubkeys/subscription ::m.n.subscription-pubkeys/pubkey
   => (? ::m.n.subscription-pubkeys/id)]
  (log/info :find-by-relay-and-code/starting {:subscription-id subscription-id
                                              :pubkey-id       pubkey-id})
  (let [db     (c.xtdb/main-db)
        query  '{:find  [?sp-id]
                 :in    [[?subscription-id ?pubkey-id]]
                 :where [[?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]
                         [?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]]}
        result (xt/q db query [subscription-id pubkey-id])
        id     (ffirst result)]
    (log/info :find-by-relay-and-code/finished {:id id})
    id))

(defn find-pubkeys-by-subscription
  [subscription-id]
  (log/info :index-by-relay/starting {:subscription-id subscription-id})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?pubkey]
                :in    [[?subscription-id]]
                :where [[?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]
                        [?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
                        [?pubkey-id ::m.n.pubkeys/hex ?pubkey]]}
        ids   (map first (xt/q db query [subscription-id]))]
    (log/info :index-ids/finished {:ids ids})
    ids))
