(ns dinsro.queries.core.chains
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.chains/id
   :pk      '?chain-id
   :clauses [[::m.c.networks/id    '?network-id]]
   :rules
   (fn [[network-id] rules]
     (->> rules
          (concat-when network-id
            ['?network-id ::m.c.networks/chain '?chain-id])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.c.chains/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.chains/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.c.chains/id => (? ::m.c.chains/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.chains/id)
      (dissoc record :xt/id))))

(>defn find-by-name
  [name]
  [::m.c.chains/name => (? ::m.c.chains/id)]
  (log/info :find-by-name/starting {:name name})
  (c.xtdb/query-value
   '{:find  [?node-id]
     :in    [[?name]]
     :where [[?node-id ::m.c.chains/name ?name]]}
   [name]))

(>defn delete!
  [id]
  [::m.c.chains/id => any?]
  (let [node (c.xtdb/get-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))
