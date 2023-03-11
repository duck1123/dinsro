(ns dinsro.queries.nostr.requests
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.requests/params => ::m.n.requests/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.requests/status "initial")
                            (assoc ::m.n.requests/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.requests/id)]
  (log/info :index-ids/starting {})
  (let [ids (c.xtdb/query-ids '{:find [?id] :where [[?id ::m.n.requests/id _]]})]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn read-record
  [id]
  [::m.n.requests/id => (? ::m.n.requests/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/info :read-record/starting {:record record})
    (when (get record ::m.n.requests/id)
      (dissoc record :xt/id))))

(>defn find-by-relay
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.requests/id)]
  (log/info :find-by-relay/starting {:relay-id relay-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?id]
               :in    [[?relay-id]]
               :where [[?id ::m.n.requests/relay ?relay-id]]}
             [relay-id])]
    (log/info :find-by-relay/finished {:ids ids})
    ids))

(>defn find-by-relay-and-code
  [relay-id code]
  [::m.n.relays/id  ::m.n.requests/code => (? ::m.n.requests/id)]
  (log/info :find-by-relay-and-code/starting {:relay-id relay-id})
  (let [id (c.xtdb/query-id
            '{:find  [?id]
              :in    [[?relay-id ?code]]
              :where [[?id ::m.n.requests/relay ?relay-id]
                      [?id ::m.n.requests/code ?code]]}
            [relay-id code])]
    (log/info :find-by-relay-and-code/finished {:id id})
    id))

(>defn delete!
  [id]
  [::m.n.requests/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all!
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn find-relay
  [request-id]
  [::m.n.requests/id => ::m.n.requests/relay]
  (log/info :find-relay/starting {:request-id request-id})
  (let [id (c.xtdb/query-id
            '{:find  [?relay-id]
              :in    [[?request-id]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]]}
            [request-id])]
    (log/info :find-relay/finished {:id id})
    id))
