(ns dinsro.queries.nostr.runs
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [../../actions/nostr/runs.clj]
;; [../../model/nostr/runs.cljc]

(>defn create-record
  [params]
  [::m.n.runs/params => ::m.n.runs/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (merge
                         {::m.n.runs/start-time  nil
                          ::m.n.runs/end-time    nil
                          ::m.n.runs/finish-time nil
                          ::m.n.runs/status      :initial
                          ::m.n.runs/id          id
                          :xt/id                 id}
                         params)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/trace :create-record/finished {:id id})
    id))

(defn get-index-query
  [query-params]
  (let [request-id (::m.n.requests/id query-params)]
    {:find  ['?run-id]
     :in    [['?request-id]]
     :where (->> [['?run-id ::m.n.runs/id '_]]
                 (concat (when request-id
                           [['?run-id ::m.n.runs/request '?request-id]]))
                 (filter identity)
                 (into []))}))

(defn get-index-params
  [query-params]
  (let [request-id (::m.n.requests/id query-params)]
    [request-id]))

(defn count-ids
  ([]
   (count-ids {}))
  ([query-params]
   (log/info :count-ids/starting {:query-params query-params})
   (let [base-params  (get-index-query query-params)
         limit-params {:find ['(count ?run-id)]}
         query        (merge base-params limit-params)
         params       []]
     (log/info :count-ids/query {:query query :params params})
     (let [c (c.xtdb/query-id query params)]
       (or c 0)))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.runs/id)]
   (index-ids {}))
  ([query-params]
   [(s/keys) => (s/coll-of ::m.n.runs/id)]
   (do
     (log/debug :index-ids/starting {})
     (let [{:indexed-access/keys [options]} query-params
           {:keys [limit offset]
            :or   {limit 20 offset 0}}      options
           base-params                      (get-index-query query-params)
           limit-params                     {:limit limit :offset offset}
           query                            (merge base-params limit-params)
           params                           (get-index-params query-params)]
       (log/info :index-ids/running {:query query :params params})
       (let [ids (c.xtdb/query-ids query params)]
         (log/trace :index-ids/finished {:ids ids})
         ids)))))

(>defn read-record
  [id]
  [::m.n.runs/id => (? ::m.n.runs/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/debug :read-record/starting {:record record})
    (when (get record ::m.n.runs/id)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.runs/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-request
  [request-id]
  [::m.n.requests/id => (s/coll-of ::m.n.runs/id)]
  (log/debug :find-by-request/starting {:request-id request-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?run-id]
               :in    [[?request-id]]
               :where [[?run-id ::m.n.runs/request ?request-id]]}
             [request-id])]
    (log/trace :find-by-request/finished {:ids ids})
    ids))

(>defn find-by-request-and-connection
  [request-id connection-id]
  [::m.n.requests/id ::m.n.connections/id => (? ::m.n.runs/id)]
  (log/debug :find-by-request-and-connection/starting {:request-id request-id :connection-id connection-id})
  (let [id (c.xtdb/query-id
            '{:find  [?run-id]
              :in    [[?request-id ?connection-id]]
              :where [[?run-id ::m.n.runs/request ?request-id]
                      [?run-id ::m.n.runs/connection ?connection-id]]}
            [request-id connection-id])]
    (log/trace :find-by-request-and-connection/finished {:id id})
    id))

(>defn find-active
  []
  [=> (s/coll-of ::m.n.runs/id)]
  (log/debug :find-connected/starting {})
  (let [ids (c.xtdb/query-ids
             '{:find  [?run-id]
               :where [(or [?run-id ::m.n.runs/status :started]
                           [?run-id ::m.n.runs/status :finished])]})]

    (log/trace :find-connected/finished {:ids ids})
    ids))

(>defn find-active-by-connection
  [connection-id]
  [::m.n.connections/id => (s/coll-of ::m.n.runs/id)]
  (log/debug :find-connected-by-connection/starting {:connection-id connection-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?run-id]
               :in    [[?connection-id]]
               :where [[?run-id ::m.n.runs/connection ?connection-id]
                       (or
                        [?run-id ::m.n.runs/status :started]
                        [?run-id ::m.n.runs/status :finished])]}
             [connection-id])]
    (log/trace :find-connected-by-connection/finished {:ids ids})
    ids))

(>defn find-active-by-code
  [code]
  [::m.n.requests/code => (s/coll-of ::m.n.runs/id)]
  (log/debug :find-active-by-code/starting {:code code})
  (let [id (c.xtdb/query-ids
            '{:find  [?run-id]
              :in    [[?code]]
              :where [[?run-id ::m.n.runs/request ?request-id]
                      (or
                       [?run-id ::m.n.runs/status :started]
                       [?run-id ::m.n.runs/status :finished])
                      [?request-id ::m.n.requests/code ?code]]}
            [code])]
    (log/trace :find-active-by-connection-and-code/finished {:id id})
    id))

(>defn find-active-by-connection-and-code
  [connection-id code]
  [::m.n.connections/id  ::m.n.requests/code => (? ::m.n.runs/id)]
  (log/debug :find-active-by-connection-and-code/starting {:connection-id connection-id :code code})
  (let [id (c.xtdb/query-id
            '{:find  [?run-id]
              :in    [[?connection-id ?code]]
              :where [[?run-id ::m.n.runs/connection ?connection-id]
                      [?run-id ::m.n.runs/request ?request-id]
                      (or
                       [?run-id ::m.n.runs/status :started]
                       [?run-id ::m.n.runs/status :finished])
                      [?request-id ::m.n.requests/code ?code]]}
            [connection-id code])]
    (log/trace :find-active-by-connection-and-code/finished {:id id})
    id))

(>defn find-by-connection
  [connection-id]
  [::m.n.connections/id => (s/coll-of ::m.n.runs/id)]
  (log/debug :find-by-connection/starting {:connection-id connection-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?run-id]
               :in    [[?connection-id]]
               :where [[?run-id ::m.n.runs/connection ?connection-id]]}
             [connection-id])]
    (log/trace :find-by-connection/finished {:ids ids})
    ids))

(defn create-set-started!
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-started!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {::m.n.runs/status     :started
                                                ::m.n.runs/start-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/await-tx node (xt/submit-tx node [[::xt/put query-def]]))))

(defn create-set-stopped!
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-stopped!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity
                                               {::m.n.runs/status     :stopped
                                                ::m.n.runs/end-time time})]
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
                                               {::m.n.runs/status   :errored
                                                ::m.n.runs/end-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/submit-tx node [[::xt/put query-def]])))

(defn create-set-finished!
  []
  (let [node (c.xtdb/main-node)
        query-def
        {:xt/id ::set-finished!
         :xt/fn '(fn [ctx eid]
                   (let [time           (dinsro.specs/->inst)
                         entity         (some-> ctx xtdb.api/db (xtdb.api/entity eid))
                         updated-entity (merge entity {::m.n.runs/status      :finished
                                                       ::m.n.runs/finish-time time})]
                     [[::xt/put updated-entity]]))}]
    (xt/submit-tx node [[::xt/put query-def]])))

(>defn set-started!
  [run-id]
  [::m.n.runs/id => any?]
  (log/debug :set-started!/starting {:run-id run-id})
  (c.xtdb/submit-tx! ::set-started! [run-id])
  nil)

(>defn set-finished!
  [run-id]
  [::m.n.runs/id => any?]
  (log/debug :set-finished!/starting {:run-id run-id})
  (c.xtdb/submit-tx! ::set-finished! [run-id])
  nil)

(>defn set-stopped!
  [run-id]
  [::m.n.runs/id => any?]
  (log/debug :set-stopped!/starting {:run-id run-id})
  (c.xtdb/submit-tx! ::set-stopped! [run-id])
  nil)

(defn initialize-queries!
  []
  (log/debug :initialize-queries!/starting {})
  ;; (create-status-setter!)
  ;; (create-set-connecting!)
  (create-set-started!)
  (create-set-finished!)
  (create-set-stopped!)
  (create-set-errored!)
  (log/trace :initialize-queries!/finished {}))
