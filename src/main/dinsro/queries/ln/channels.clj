(ns dinsro.queries.ln.channels
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.ln.channels/id
   :pk      '?channel-id
   :clauses [[::m.ln.nodes/id '?node-id]]
   :rules
   (fn [[node-id] rules]
     (->> rules
          (concat-when node-id
            [['?ln-channels-id ::m.ln.channels/node '?node-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.channels/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.channels/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.channels/params => ::m.ln.channels/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.channels/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-channel
  [node-id channel-point]
  [::m.ln.channels/node ::m.ln.channels/channel-point => (? ::m.ln.channels/id)]
  (c.xtdb/query-value
   '{:find  [?channel-id]
     :in    [[?node-id ?channel-point]]
     :where [[?channel-id ::m.ln.channels/node ?node-id]
             [?channel-id ::m.ln.channels/channel-point ?channel-point]]}
   [node-id channel-point]))

(>defn delete!
  [id]
  [::m.ln.channels/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

(>defn update!
  [params]
  [::m.ln.channels/item => ::m.ln.channels/id]
  (if-let [id (::m.ln.channels/id params)]
    (let [node   (c.xtdb/get-node)
          params (assoc params :xt/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (ex-info "Failed to find id" {}))))
