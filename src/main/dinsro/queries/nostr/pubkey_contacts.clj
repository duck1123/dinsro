(ns dinsro.queries.nostr.pubkey-contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.pubkey-contacts/id)]
  (let [db    (c.xtdb/main-db)
        query '[:find ?e :where [?e ::m.n.pubkey-contacts/id _]]]
    (map first (xt/q db query))))
