(ns dinsro.queries.ln.remote-nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.ln.remote-nodes/id
   :pk      '?remote-node-id
   :clauses [[::m.ln.nodes/id '?node-id]]
   :rules
   (fn [[node-id] rules]
     (->> rules
          (concat-when node-id
            [['?remote-node-id ::m.ln.remote-nodes/node '?node-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.remote-nodes/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.remote-nodes/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.remote-nodes/params => ::m.ln.remote-nodes/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.remote-nodes/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-node-and-pubkey
  [node-id pubkey]
  [::m.ln.remote-nodes/node ::m.ln.remote-nodes/pubkey => (? ::m.ln.remote-nodes/id)]
  (c.xtdb/query-value
   '{:find  [?remote-node-id]
     :in    [[?node-id ?pubkey]]
     :where [[?remote-node-id ::m.ln.remote-nodes/pubkey ?pubkey]
             [?remote-node-id ::m.ln.remote-nodes/node ?node-id]]}
   [node-id pubkey]))

(>defn find-channel
  [node-id channel-point]
  [::m.ln.remote-nodes/node ::m.ln.remote-nodes/channel-point => (? ::m.ln.remote-nodes/id)]
  (c.xtdb/query-values
   '{:find  [?channel-id]
     :in    [[?node-id ?channel-point]]
     :where [[?channel-id ::m.ln.remote-nodes/node ?node-id]
             [?channel-id ::m.ln.remote-nodes/channel-point ?channel-point]]}
   [node-id channel-point]))

(>defn delete!
  [id]
  [::m.ln.remote-nodes/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

(>defn update!
  [params]
  [::m.ln.remote-nodes/item => ::m.ln.remote-nodes/id]
  (if-let [id (::m.ln.remote-nodes/id params)]
    (let [node   (c.xtdb/get-node)
          params (assoc params :xt/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (ex-info "Failed to find id" {}))))
