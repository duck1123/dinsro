(ns dinsro.queries.nostr.connections
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.options.nostr.connections :as o.n.connections]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/connections.clj]]
;; [[../../joins/nostr/connections.cljc]]
;; [[../../model/nostr/connections.cljc]]
;; [[../../processors/nostr/connections.clj]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/connections_notebook.clj]]

(def model-key o.n.connections/id)

(def query-info
  {:ident   model-key
   :pk      '?connection-id
   :clauses [[::m.n.requests/id '?request-id]
             [::m.n.relays/id   '?relay-id]
             [::m.instances/id  '?instance-id]]
   :rules
   (fn [[request-id relay-id instance-id] rules]
     (->> rules
          (concat-when request-id
            [['?request-id    ::m.n.requests/connection '?connection-id]])
          (concat-when relay-id
            [['?connection-id o.n.connections/relay     '?relay-id]])
          (concat-when instance-id
            [['?connection-id o.n.connections/instance  '?instance-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.connections/params => ::m.n.connections/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.connections/id => (? ::m.n.connections/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.connections/id => nil?]
  (c.xtdb/delete! id))

(>defn set-connecting!
  [id]
  [::m.n.connections/id => any?]
  (log/debug :set-connecting!/starting {:id id})
  (c.xtdb/submit-tx! ::set-connecting! [id]))

(>defn set-connected!
  [id]
  [::m.n.connections/id => any?]
  (log/debug :set-connected!/starting {:id id})
  (c.xtdb/submit-tx! ::set-status! [id :connected]))

(>defn set-errored!
  [id]
  [::m.n.connections/id => any?]
  (log/debug :set-errored!/starting {:id id})
  (c.xtdb/submit-tx! ::set-errored! [id]))

(>defn set-disconnected!
  [id]
  [::m.n.connections/id => any?]
  (log/debug :set-disconnected!/starting {:id id})
  (c.xtdb/submit-tx! ::set-disconnected! [id]))

(defn create-set-connecting!
  []
  (let [node (c.xtdb/get-node)
        query-def
        {:xt/id ::set-connecting!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {o.n.connections/status     :connecting
                                                o.n.connections/start-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-set-disconnected!
  "create set-disconnected transaction"
  []
  (let [node (c.xtdb/get-node)
        query-def
        {:xt/id ::set-disconnected!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {o.n.connections/status   :disconnected
                                                ::m.n.connections/end-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-set-errored!
  []
  (let [node (c.xtdb/get-node)
        query-def
        {:xt/id ::set-errored!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {o.n.connections/status   :errored
                                                o.n.connections/end-time time})]
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
        node      (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn find-by-relay
  [relay-id]
  (c.xtdb/query-values
   '{:find  [?connection-id]
     :in    [[?relay-id]]
     :where [[?connection-id o.n.connections/relay ?relay-id]]}
   [relay-id]))

(defn find-connected
  []
  (let [ids (c.xtdb/query-values
             '{:find  [?connection-id]
               :where [[?connection-id ::m.n.connections/status :connected]]})]
    (log/trace :find-connected/finished {:ids ids})
    ids))

(defn find-connected-by-relay
  [relay-id]
  (log/debug :find-connected-by-relay/starting {:relay-id relay-id})
  (let [id (c.xtdb/query-value
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
