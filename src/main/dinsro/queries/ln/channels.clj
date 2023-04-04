(ns dinsro.queries.ln.channels
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln.channels/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.ln.channels/id _]]}))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.channels/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.channels/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.channels/params => ::m.ln.channels/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.channels/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-node
  [node-id]
  [::m.ln.nodes/id => (s/coll-of ::m.ln.channels/id)]
  (c.xtdb/query-ids
   '{:find  [?channel-id]
     :in    [[?node-id]]
     :where [[?channel-id ::m.ln.channels/node [?node-id]]]}
   [node-id]))

(>defn find-channel
  [node-id channel-point]
  [::m.ln.channels/node ::m.ln.channels/channel-point => (? ::m.ln.channels/id)]
  (c.xtdb/query-id
   '{:find  [?channel-id]
     :in    [[?node-id ?channel-point]]
     :where [[?channel-id ::m.ln.channels/node ?node-id]
             [?channel-id ::m.ln.channels/channel-point ?channel-point]]}
   [node-id channel-point]))

(>defn delete!
  [id]
  [::m.ln.channels/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

(>defn update!
  [params]
  [::m.ln.channels/item => ::m.ln.channels/id]
  (if-let [id (::m.ln.channels/id params)]
    (let [node   (c.xtdb/main-node)
          params (assoc params :xt/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (ex-info "Failed to find id" {}))))
