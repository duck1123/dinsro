(ns dinsro.queries.ln.payments
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.ln.payments/id
   :pk      '?payment-id
   :clauses [[::m.ln.nodes/id '?nodes-id]]
   :rules
   (fn [[nodes-id] rules]
     (->> rules
          (concat-when nodes-id
            [['?payment-id ::m.ln.payments/node '?node-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.payments/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.payments/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.payments/params => ::m.ln.payments/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.payments/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-node
  [node-id]
  [::m.ln.nodes/id => (s/coll-of ::m.ln.payments/id)]
  (c.xtdb/query-values '{:find  [?payment-id]
                         :in    [[?node-id]]
                         :where [[?payment-id ::m.ln.payments/node ?node-id]]}
                       [node-id]))

(>defn delete!
  [id]
  [::m.ln.payments/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)
