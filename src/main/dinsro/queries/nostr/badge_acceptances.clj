(ns dinsro.queries.nostr.badge-acceptances
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.badge-acceptances/params => ::m.n.badge-acceptances/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.badge-acceptances/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.badge-acceptances/id => (? ::m.n.badge-acceptances/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.badge-acceptances/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.badge-acceptances/id)]
  (c.xtdb/query-ids '{:find [?relay-id] :where [[?relay-id ::m.n.badge-acceptances/id _]]}))
