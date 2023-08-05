(ns dinsro.queries.nostr.subscriptions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/subscriptions.clj]]

(def model-key ::m.n.subscriptions/id)

(def query-info
  {:ident   model-key
   :pk      '?subscription-id
   :clauses [[::m.n.relays '?relay-id]]
   :rules
   (fn [[relay-id] rules]
     (->> rules
          (concat-when relay-id
            [['?subscription-id ::m.n.subscriptions/relay '?relay-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.subscriptions/params => ::m.n.subscriptions/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc model-key id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn find-by-relay-and-pubkey
  [relay-id pubkey-id]
  [::m.n.relays/id ::m.n.pubkeys/id => (? ::m.n.subscriptions/id)]
  (log/debug :find-by-relay-and-pubkey/starting {:relay-id relay-id :pubkey-id pubkey-id})
  (c.xtdb/query-value
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
  (c.xtdb/query-values
   '{:find  [?subscription-id]
     :in    [[?pubkey-id]]
     :where [[?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
             [?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]]}
   [pubkey-id]))

(>defn read-record
  [id]
  [::m.n.subscriptions/id => (? ::m.n.subscriptions/item)]
  (log/debug :read-record/starting {:id id})
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (model-key record)
      (let [read-record (dissoc record :xt/id)]
        (log/info :read-record/read {:read-record read-record})
        read-record))))

(>defn find-by-relay-and-code
  [relay-id code]
  [::m.n.relays/id ::m.n.subscriptions/code => (? ::m.n.subscriptions/id)]
  (log/debug :find-by-relay-and-code/starting {:relay-id relay-id :code code})
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?relay-id ?code]]
     :where [[?id ::m.n.subscriptions/relay ?relay-id]
             [?id ::m.n.subscriptions/code ?code]]}
   [relay-id code]))

(>defn find-by-relay
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.subscriptions/id)]
  (log/debug :find-by-relay/starting {:relay-id relay-id})
  (c.xtdb/query-values
   '{:find  [?id]
     :in    [[?relay-id]]
     :where [[?id ::m.n.subscriptions/relay ?relay-id]]}
   [relay-id]))
