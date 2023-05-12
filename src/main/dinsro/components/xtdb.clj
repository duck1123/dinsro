(ns dinsro.components.xtdb
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.components.config :refer [get-config]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as rad.xt]
   [xtdb.api :as xt]))

(declare xtdb-nodes)
(def default-db-key :main)

(>def ::ident
  "A keywork representing an attribute"
  keyword?)

(>def ::pk
  "A symbol representing a placeholder in the query"
  symbol?)

(>def ::order-direction #{:asc :desc})
(>def ::order-by-clause (s/tuple ::pk ::order-direction))
(>def ::order-by (s/coll-of ::order-by-clause))
(>def ::additional-clauses (s/coll-of ::pk))

(>def ::clause (s/tuple ::ident ::pk))
(>def ::clauses (s/coll-of ::clause))

(>def ::query-info
  (s/keys
   :req-un [::ident ::pk ::clauses ::rules]
   :opt-un [::additional-clauses ::additional-rules ::order-by]))

(defn concat-when
  [pred? rules existing-rules]
  (concat existing-rules (when pred? rules)))

(>def ::limit number?)
(>def ::offset number?)
(>def ::limit-options (s/keys :req-un [::limit ::offset]))

(>defn get-limit-options
  "Parse pagination arguments from query-params"
  [query-params]
  [map? => ::limit-options]
  (log/debug :get-limit-options/starting {:query-params query-params})
  (let [{:indexed-access/keys [options]} query-params
        {:keys [limit offset]
         :or   {limit 20 offset 0}}      options
        limit-options {:limit limit :offset offset}]
    (log/debug :get-limit-options/finished {:limit-options limit-options})
    limit-options))

(defn start-database!
  "Start the xtdb database"
  ([]
   (let [conf (rad.xt/symbolize-xtdb-modules (get-config))]
     (start-database! conf)))
  ([conf]
   (log/info :start-database!/starting {:conf conf})
   (let [node (rad.xt/start-databases conf)]
     (log/trace :start-database!/finished {:conf conf :node node})
     node)))

(defn stop-database!
  "Start the xtdb database"
  []
  (log/info :stop-database!/starting {})
  (let [nodes @xtdb-nodes]
    (for [node nodes] (.close node))))

(defstate ^{:on-reload :noop} xtdb-nodes
  "A collection of started xtdb nodes"
  :start (start-database!)
  :stop (stop-database!))

(defn get-node
  "Returns the main xtdb node"
  ([] (get-node default-db-key))
  ([db-key]
   (let [nodes @xtdb-nodes]
     (log/trace :nodes/read {:nodes nodes})
     (db-key nodes))))

(>defn get-db
  "Returns the main xtdb database"
  ([]
   [=> any?]
   (get-db default-db-key))
  ([db-key]
   [keyword? => any?]
   (let [node (get-node db-key)
         db   (xt/db node)]
     (log/trace :get-db/read {:db db :node node})
     db)))

(>defn submit-tx!
  "Submit a transaction to the main db"
  [k params]
  [keyword? map? => any?]
  (let [ops [(concat [::xt/fn k] params)]]
    (log/trace :submit-tx/starting {:ops ops})
    (let [node     (get-node)
          response (xt/submit-tx node ops)]
      (log/trace :submit-tx!/finished {:response response})
      response)))

(>defn run-query
  "Run a query against the main database"
  ([query]
   [map? => any?]
   (do
     (log/trace :run-query/starting {:query query})
     (let [db      (get-db)
           results (xt/q db query)]
       (log/trace :run-query/finished {:results results})
       results)))

  ([query params]
   [map? vector? => any?]
   (do
     (log/trace :run-query/starting {:query query :params params})
     (let [db      (get-db)
           results (xt/q db query params)]
       (log/trace :run-query/finished {:results results})
       results))))

(>defn query-value
  "Run a query to produce a single value"
  ([query]
   [map? => any?]
   (do
     (log/trace :query-value/starting {:query query})
     (let [results (run-query query)
           id      (ffirst results)]
       (log/trace :query-value/finished {:id id :results results})
       id)))

  ([query params]
   [map? vector? => any?]
   (do
     (log/trace :query-value/starting {:query query :params params})
     (let [results (run-query query params)
           id      (ffirst results)]
       (log/trace :query-value/finished {:id id :results results})
       id))))

(>defn query-values
  "Run a query to produce a sequence of single values"
  ([query]
   [map? => (s/coll-of any?)]
   (log/trace :query-values/starting {:query query})
   (let [ids (map first (run-query query))]
     (log/trace :query-values/finished {:ids ids})
     ids))
  ([query params]
   [map? vector? => (s/coll-of any?)]
   (log/trace :query-values/starting {:query query :params params})
   (let [ids (map first (run-query query params))]
     (log/trace :query-values/finished {:ids ids})
     ids)))

(>defn get-index-params
  "Returns a vector of values from taking each key in the clauses section from the query params"
  [query-info query-params]
  [::query-info map? => (s/coll-of any?)]
  (log/debug :get-index-params {:query-info query-info :query-params query-params})
  (let [{:keys [clauses]} query-info
        params            (mapv (fn [[key _]] (get query-params key)) clauses)]
    (log/debug :get-index-params/finished {:params params})
    params))

(defn make-index-query
  "Takes the definition of a query and the porvided params and returns a base query map."
  [query-info query-params]
  (log/trace :make-index-query/starting {:query-info query-info :query-params query-params})
  (let [{:keys [pk ident clauses rules additional-rules additional-clauses]
         :or {additional-clauses []
              additional-rules   []}} query-info
        index-params                        (get-index-params query-info query-params)
        query    {:find  (vec (concat [pk] additional-clauses))
                  :in    [(mapv (fn [[_ s]] s) clauses)]
                  :where (->> [[pk ident '_]]
                              (concat additional-rules)
                              (rules index-params)
                              (filter identity)
                              (into []))}]
    (log/trace :make-index-query/finished {:query query})
    query))

(>defn count-ids
  "Count the records matching the query"
  [query-info query-params]
  [::query-info map? => number?]
  (log/debug :count-ids/starting {:query-info query-info :query-params query-params})
  (let [{:keys [pk]} query-info
        base-params  (make-index-query query-info query-params)
        limit-params {:find [(list 'count pk)]}
        params       (get-index-params query-info query-params)
        query        (merge base-params limit-params)]
    (log/info :count-ids/query {:query query :params params})
    (let [n (or (query-value query params) 0)]
      (log/debug :count-ids/finished {:n n})
      n)))

(>defn order-query
  [query query-info query-params]
  [map? ::query-info map? => map?]
  (log/info :order-query/starting {:query query :query-info query-info :query-params query-params})
  (if-let [sort-column (get-in query-params [:indexed-access/options :sort-column])]
    (let [reverse?                  (get-in query-params [:indexed-access/options :reverse?])
          {:keys [find where]}      query
          {:keys [sort-columns pk]
           :or   {sort-columns {}}} query-info]
      (if-let [sort-sym (get sort-columns sort-column)]
        (let [sort-rule     [[pk sort-column sort-sym]]
              new-params    {:find     (vec (concat find [sort-sym]))
                             :where    (vec (concat where sort-rule))
                             :order-by [[sort-sym (if reverse? :desc :asc)]]}
              ordered-query (merge query new-params)]
          (log/info :order-query/finished {:ordered-query ordered-query})
          ordered-query)
        (throw (ex-info "no sym" {:sort-column sort-column}))))
    query))

(>defn index-ids
  "Returns the ids of each record matching the query"
  [query-info query-params]
  [::query-info map? => (s/coll-of uuid?)]
  (log/trace :index-ids/starting {:query-info query-info :query-params query-params})
  (let [base-params  (make-index-query query-info  query-params)
        limit-params (get-limit-options query-params)
        query        (merge base-params limit-params)
        query        (order-query query query-info query-params)
        params       (get-index-params query-info query-params)]
    (log/debug :index-ids/query {:query query :params params})
    (let [ids (query-values query params)]
      (log/trace :index-ids/finished {:ids ids})
      ids)))

(>defn delete!
  "Delete the record with this id"
  [id]
  [:xt/id => nil?]
  (let [node (get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)
