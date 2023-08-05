(ns dinsro.queries.nostr.filters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../../../notebooks/dinsro/notebooks/nostr/filters_notebook.clj]]

(def model-key ::m.n.filters/id)

(def query-info
  {:ident   model-key
   :pk      '?filter-id
   :clauses [[::m.n.requests/id '?request-id]]
   :rules
   (fn [[request-id] rules]
     (->> rules
          (concat-when request-id
            [['?filter-id ::m.n.filters/request '?request-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.filters/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.filters/id => (? ::m.n.filters/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (model-key record)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.filters/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn get-greatest-index
  "Returns the largest index of all filters matching this request"
  [request-id]
  [::m.n.requests/id => number?]
  (let [db (c.xtdb/get-db)
        query '{:find [?index]
                :in [[?request-id]]
                :where [[?filter-id ::m.n.filters/index ?index]
                        [?filter-id ::m.n.filters/request ?request-id]
                        (not-join [?index]
                                  [?other-filter ::m.n.filters/index ?other-index]
                                  [?other-filter ::m.n.filters/request ?request-id]
                                  [(> ?other-index ?index)])]}
        result (xt/q db query [request-id])]
    (log/info :get-greatest-index/result {:result result})
    (or (ffirst result) -1)))

(>defn find-by-request
  [request-id]
  [::m.n.requests/id => (s/coll-of ::m.n.filters/id)]
  (c.xtdb/query-values
   '{:find [?filter-id]
     :in [[?request-id]]
     :where [[?filter-id ::m.n.filters/request ?request-id]]}
   [request-id]))
