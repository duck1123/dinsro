(ns dinsro.queries.ln.invoices
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.ln.invoices/id
   :pk      '?invoice-id
   :clauses [[::m.ln.nodes/id '?ln-node-id]]
   :rules
   (fn [[ln-node-id] rules]
     (->> rules
          (concat-when ln-node-id
            [['?invoice-id ::m.ln.invoices/node '?ln-node-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.invoices/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.invoices/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.invoices/params => ::m.ln.invoices/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.invoices/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-node
  [node-id]
  [::m.ln.nodes/id => (s/coll-of ::m.ln.invoices/id)]
  (c.xtdb/query-values
   '{:find  [?invoice-id]
     :in    [[?node-id]]
     :where [[?invoice-id ::m.ln.invoices/node ?node-id]]}
   [node-id]))

(>defn find-by-node-and-index
  [node-id index]
  [::m.ln.nodes/id number? => (? ::m.ln.invoices/id)]
  (c.xtdb/query-value
   '{:find  [?invoice-id]
     :in    [[?node-id ?index]]
     :where [[?invoice-id ::m.ln.invoices/node ?node-id]
             [?invoice-id ::m.ln.invoices/add-index ?index]]}
   [node-id index]))

(>defn delete!
  [id]
  [::m.ln.invoices/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

(>defn update!
  [params]
  [::m.ln.invoices/item => ::m.ln.invoices/id]
  (if-let [id (::m.ln.invoices/id params)]
    (let [node   (c.xtdb/get-node)
          params (assoc params :xt/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (ex-info "Failed to find id" {}))))
