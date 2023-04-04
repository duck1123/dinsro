(ns dinsro.queries.nostr.badge-definitions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.badge-definitions/params => ::m.n.badge-definitions/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.badge-definitions/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.badge-definitions/id => (? ::m.n.badge-definitions/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.badge-definitions/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.badge-definitions/id)]
  (c.xtdb/query-ids '{:find [?relay-id] :where [[?relay-id ::m.n.badge-definitions/id _]]}))
