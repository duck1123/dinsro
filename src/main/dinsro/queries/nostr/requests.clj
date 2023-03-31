(ns dinsro.queries.nostr.requests
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.requests/params => ::m.n.requests/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (merge
                         {::m.n.requests/start-time nil
                          ::m.n.requests/end-time   nil
                          ::m.n.requests/status     "initial"
                          ::m.n.requests/id         id
                          :xt/id                    id}
                         params)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.requests/id)]
  (log/debug :index-ids/starting {})
  (let [ids (c.xtdb/query-ids '{:find [?id] :where [[?id ::m.n.requests/id _]]})]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn read-record
  [id]
  [::m.n.requests/id => (? ::m.n.requests/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/debug :read-record/starting {:record record})
    (when (get record ::m.n.requests/id)
      (dissoc record :xt/id))))

(>defn find-by-relay
  "Find all requests for relay"
  [relay-id]
  [::m.n.relays/id => (s/coll-of ::m.n.requests/id)]
  (log/debug :find-by-relay/starting {:relay-id relay-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?id]
               :in    [[?relay-id]]
               :where [[?id ::m.n.requests/relay ?relay-id]]}
             [relay-id])]
    (log/trace :find-by-relay/finished {:ids ids})
    ids))

(>defn find-by-code
  [code]
  [::m.n.requests/code => (s/coll-of ::m.n.requests/id)]
  (log/debug :find-by-relay-and-code/starting {:code code})
  (let [id (c.xtdb/query-ids
            '{:find  [?request-id]
              :in    [[?code]]
              :where [[?request-id ::m.n.requests/code ?code]]}
            [code])]
    (log/trace :find-by-relay-and-code/finished {:id id})
    id))

(>defn find-by-relay-and-code
  "Find request for relay with code"
  [relay-id code]
  [::m.n.relays/id  ::m.n.requests/code => (? ::m.n.requests/id)]
  (log/debug :find-by-relay-and-code/starting {:relay-id relay-id :code code})
  (let [id (c.xtdb/query-id
            '{:find  [?request-id]
              :in    [[?relay-id ?code]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]
                      [?request-id ::m.n.requests/code ?code]]}
            [relay-id code])]
    (log/trace :find-by-relay-and-code/finished {:id id})
    id))

(defn find-by-connection-and-code
  [connection-id code]
  (log/debug :find-by-connection-and-code/starting {:connection-id connection-id :code code})
  (let [id (c.xtdb/query-id
            '{:find  [?request-id]
              :in    [[?connection-id ?code]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]
                      [?request-id ::m.n.requests/code ?code]
                      [?connnection-id ::m.n.connections/relay ?relay-id]]}
            [connection-id code])]
    (log/trace :find-by-connection-and-code/finished {:id id})
    id))

(>defn find-by-filter-item
  [filter-item-id]
  [::m.n.filter-items/id => (? ::m.n.requests/id)]
  (log/debug :find-by-filter-item/starting {:filter-item-id filter-item-id})
  (let [id (c.xtdb/query-id
            '{:find  [?request-id]
              :in    [[?filter-item-id]]
              :where [[?filter-id ::m.n.filters/request ?request-id]
                      [?filter-item-id ::m.n.filter-items/filter ?filter-id]]}
            [filter-item-id])]
    (log/trace :find-filter-item/finished {:id id})
    id))

(>defn find-by-run
  [run-id]
  [::m.n.runs/id => (? ::m.n.requests/id)]
  (log/debug :find-by-run/starting {:run-id run-id})
  (let [id (c.xtdb/query-id
            '{:find  [?request-id]
              :in    [[?run-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]]}
            [run-id])]
    (log/trace :find-by-run/finished {:id id})
    id))

(>defn find-code-by-run
  [run-id]
  [::m.n.runs/id => (? string?)]
  (log/debug :find-code-by-run/starting {:run-id run-id})
  (let [id (c.xtdb/query-id
            '{:find  [?code]
              :in    [[?run-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]
                      [?request-id ::m.n.requests/code ?code]]}
            [run-id])]
    (log/finer :find-code-by-run/finished {:id id})
    id))

(>defn delete!
  "Delete request by id"
  [id]
  [::m.n.requests/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all!
  "Delete all requests"
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn find-relay
  "Find the relay id for the request"
  [request-id]
  [::m.n.requests/id => (? ::m.n.requests/relay)]
  (log/debug :find-relay/starting {:request-id request-id})
  (let [id (c.xtdb/query-id
            '{:find  [?relay-id]
              :in    [[?request-id]]
              :where [[?request-id ::m.n.requests/relay ?relay-id]]}
            [request-id])]
    (log/trace :find-relay/finished {:id id})
    id))

(defn set-started
  [request-id]
  (log/debug :set-stopped/starting {:request-id request-id})
  (c.xtdb/submit-tx! ::set-started request-id))

(defn set-stopped
  [request-id]
  (log/debug :set-stopped/starting {:request-id request-id})
  (c.xtdb/submit-tx! ::set-stopped request-id))

(>defn create-set-started
  []
  [=> any?]
  (log/debug :create-set-started/starting {})
  (let [node             (c.xtdb/main-node)
        toggle-connected {:xt/id ::set-started
                          :xt/fn '(fn [ctx eid]
                                    (let [entity (some-> ctx xtdb.api/db (xtdb.api/entity eid)
                                                         (assoc ::m.n.requests/status "started")
                                                         (assoc ::m.n.requests/start-time (dinsro.specs/->inst)))]
                                      [[::xt/put entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put toggle-connected]]))
    (log/trace :create-set-started/finished {})))

(>defn create-set-stopped
  "create set-stopped transaction"
  []
  [=> any?]
  (log/debug :create-set-stopped/starting {})
  (let [node             (c.xtdb/main-node)
        toggle-connected {:xt/id ::set-stopped
                          :xt/fn '(fn [ctx eid]
                                    (let [entity (some-> ctx xtdb.api/db (xtdb.api/entity eid)
                                                         (assoc ::m.n.requests/status "stopped")
                                                         (assoc ::m.n.requests/end-time (dinsro.specs/->inst)))]
                                      [[::xt/put entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put toggle-connected]]))
    (log/trace :create-set-stopped/finished {})))

(>defn initialize-queries!
  []
  [=> any?]
  (log/debug :initialize-queries!/starting {})
  (create-set-started)
  (create-set-stopped)
  (log/trace :initialize-queries!/finished {}))
