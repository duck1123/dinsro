(ns dinsro.queries.ln.payreqs
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln.payreqs/id)]
  (c.xtdb/query-ids '{:find  [?id]
                      :where [[?id ::m.ln.payreqs/id _]]}))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.payreqs/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.payreqs/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.payreqs/params => ::m.ln.payreqs/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.payreqs/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-node
  [node-id]
  [::m.ln.nodes/id => (s/coll-of ::m.ln.payreqs/id)]
  (c.xtdb/query-ids
   '{:find  [?payment-id]
     :in    [[?node-id]]
     :where [[?payment-id ::m.ln.payreqs/node ?node-id]]}
   [node-id]))

(>defn delete!
  [id]
  [::m.ln.payreqs/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)
