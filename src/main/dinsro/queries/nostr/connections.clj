(ns dinsro.queries.nostr.connections
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def ident-key ::m.n.connections/id)
(def params-key ::m.n.connections/params)
(def item-key ::m.n.connections/item)

(>defn create-record
  [params]
  [::m.n.connections/params => ::m.n.connections/id]
  (log/debug :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ident-key id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.n.connections/id => (? ::m.n.connections/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ident-key)
      (dissoc record :xt/id))))

(defn get-index-query
  [query-params]
  (let [{request-id ::m.n.requests/id} query-params]
    {:find  ['?connection-id]
     :in    [['?request-id]]
     :where (->> [['?connection-id ::m.n.connections/id '_]]
                 (concat (when request-id
                           [['?request-id ::m.n.requests/connection '?connection-id]]))
                 (filter identity)
                 (into []))}))

(defn get-index-params
  [query-params]
  (let [{request-id ::m.n.requests/id} query-params]
    [request-id]))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [map? => number?]
   (do
     (log/info :count-ids/starting {:query-params query-params})
     (let [base-query  (get-index-query query-params)
           limit-query {:find ['(count ?connection-id)]}
           query       (merge base-query limit-query)
           params      (get-index-params query-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-one query params)]
         (log/trace :count-ids/finished {:n n})
         n)))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.connections/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.n.connections/id)]
   (do
     (log/info :index-ids/starting {:query-params query-params})
     (let [params                           (get-index-params query-params)
           base-query                       (get-index-query query-params)
           {:indexed-access/keys [options]} query-params
           {:keys [limit offset]
            :or   {limit 20 offset 0}}      options
           limit-query                      {:limit limit :offset offset}
           query                            (merge base-query limit-query)]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-many query params)]
         (log/info :index-ids/finished {:ids ids})
         ids)))))

(>defn delete!
  [id]
  [::m.n.connections/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(defn set-connecting!
  [connection-id]
  (log/debug :set-connecting!/starting {:connection-id connection-id})
  (c.xtdb/submit-tx! ::set-connecting! [connection-id]))

(defn set-connected!
  [connection-id]
  (log/debug :set-connecting!/starting {:connection-id connection-id})
  (c.xtdb/submit-tx! ::set-status! [connection-id :connected]))

(defn set-errored!
  [connection-id]
  (log/debug :set-errored!/starting {:connection-id connection-id})
  (c.xtdb/submit-tx! ::set-errored! [connection-id]))

(defn set-disconnected!
  [connection-id]
  (log/debug :set-closed!/starting {:connection-id connection-id})
  (c.xtdb/submit-tx! ::set-disconnected! [connection-id]))

(defn create-set-connecting!
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-connecting!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {::m.n.connections/status     :connecting
                                                ::m.n.connections/start-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-set-disconnected!
  "create set-disconnected transaction"
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-disconnected!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {::m.n.connections/status   :disconnected
                                                ::m.n.connections/end-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-set-errored!
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-errored!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {::m.n.connections/status   :errored
                                                ::m.n.connections/end-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-status-setter!
  []
  (let [query-def {:xt/id ::set-status!
                   :xt/fn '(fn [ctx connection-id status]
                             (let [db           (xtdb.api/db ctx)
                                   entity       (xtdb.api/entity db connection-id)
                                   updated-data (assoc entity ::m.n.connections/status status)]
                               [[::xt/put updated-data]]))}
        node      (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn find-by-relay
  [relay-id]
  (c.xtdb/query-ids
   '{:find  [?connection-id]
     :in    [[?relay-id]]
     :where [[?connection-id ::m.n.connections/relay ?relay-id]]}
   [relay-id]))

(defn find-connected
  []
  (let [ids (c.xtdb/query-ids
             '{:find  [?connection-id]
               :where [[?connection-id ::m.n.connections/status :connected]]})]
    (log/trace :find-connected/finished {:ids ids})
    ids))

(defn find-connected-by-relay
  [relay-id]
  (log/debug :find-connected-by-relay/starting {:relay-id relay-id})
  (let [id (c.xtdb/query-id
            '{:find  [?connection-id]
              :in    [[?relay-id]]
              :where [[?connection-id ::m.n.connections/status :connected]
                      [?connection-id ::m.n.connections/relay ?relay-id]]}
            [relay-id])]
    (log/trace :find-connected-by-relay {:id id})
    id))

(defn initialize-queries!
  []
  (log/debug :initialize-queries!/starting {})
  (create-status-setter!)
  (create-set-connecting!)
  (create-set-disconnected!)
  (create-set-errored!)
  (log/trace :initialize-queries!/finished {}))
