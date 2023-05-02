(ns dinsro.queries.nostr.witnesses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [../../actions/nostr/witnesses.clj]
;; [../../joins/nostr/witnesses.cljc]
;; [../../ui/nostr/events/witnesses.cljs]
;; [../../ui/nostr/relays/witnesses.cljs]

(>defn create-record
  [params]
  [::m.n.witnesses/params => ::m.n.witnesses/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (merge
                         {::m.n.witnesses/id id
                          :xt/id             id}
                         params)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(defn get-index-query
  [query-params]
  (let [{connection-id ::m.n.connections/id
         event-id      ::m.n.events/id
         pubkey-id     ::m.n.pubkeys/id
         relay-id      ::m.n.relays/id
         run-id        ::m.n.runs/id} query-params]
    {:find  ['?witness-id]
     :in    [['?connection-id '?event-id '?pubkey-id '?relay-id '?run-id]]
     :where (->> [['?witness-id ::m.n.witnesses/id '_]]
                 (concat (when event-id
                           [['?witness-id ::m.n.witnesses/event '?event-id]]))
                 (concat (when pubkey-id
                           [['?witness-id ::m.n.witnesses/event '?pubkey-event-id]
                            ['?pubkey-event-id ::m.n.events/pubkey '?pubkey-id]]))
                 (concat (when run-id
                           [['?witness-id ::m.n.witnesses/run '?run-id]]))
                 (concat (when connection-id
                           [['?witness-id ::m.n.witnesses/run '?connection-run-id]
                            ['?connection-run-id ::m.n.runs/connection '?relay-connection-id]]))
                 (concat (when relay-id
                           [['?witness-id ::m.n.witnesses/run '?relay-run-id]
                            ['?relay-run-id ::m.n.runs/connection '?relay-connection-id]
                            ['?relay-connection-id ::m.n.connections/relay '?relay-id]]))
                 (filter identity)
                 (into []))}))

(defn get-index-params
  [query-params]
  (let [{connection-id ::m.n.connections/id
         event-id      ::m.n.events/id
         pubkey-id     ::m.n.pubkeys/id
         relay-id      ::m.n.relays/id
         run-id        ::m.n.runs/id} query-params]
    [connection-id event-id pubkey-id relay-id run-id]))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [map? => number?]
   (do
     (log/debug :count-ids/starting {:query-params query-params})
     (let [base-params  (get-index-query query-params)
           limit-params {:find ['(count ?witness-id)]}
           params       (get-index-params query-params)
           query        (merge base-params limit-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-one query params)]
         (log/info :count-ids/finished {:n n})
         (or n 0))))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.witnesses/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.n.witnesses/id)]
   (do
     (log/debug :index-ids/starting {})
     (let [{:indexed-access/keys [options]}                 query-params
           {:keys [limit options] :or {limit 20 options 0}} options
           base-params                                      (get-index-query query-params)
           limit-params                                     {:limit limit :options options}
           query                                            (merge base-params limit-params)
           params                                           (get-index-params query-params)]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-many query params)]
         (log/info :index-ids/finished {:ids ids})
         ids)))))

(>defn read-record
  [id]
  [::m.n.witnesses/id => (? ::m.n.witnesses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/debug :read-record/starting {:record record})
    (when (get record ::m.n.witnesses/id)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.witnesses/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-event-and-run
  [event-id run-id]
  [::m.n.events/id  ::m.n.runs/id => (? ::m.n.witnesses/id)]
  (log/trace :find-by-event-and-run/starting {:event-id event-id :run-id run-id})
  (c.xtdb/query-id
   '{:find  [?witness-id]
     :in    [[?event-id ?run-id]]
     :where [[?witness-id ::m.n.witnesses/event ?event-id]
             [?witness-id ::m.n.witnesses/run ?run-id]]}
   [event-id run-id]))