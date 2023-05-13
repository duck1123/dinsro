(ns dinsro.queries.nostr.badge-awards
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.badge-awards/params => ::m.n.badge-awards/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.badge-awards/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.badge-awards/id => (? ::m.n.badge-awards/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.badge-awards/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.badge-awards/id)]
  (c.xtdb/query-values '{:find [?relay-id] :where [[?relay-id ::m.n.badge-awards/id _]]}))
