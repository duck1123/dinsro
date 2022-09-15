(ns dinsro.queries.core.mnemonics
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.mnemonics/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.mnemonics/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.c.mnemonics/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.mnemonics/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.mnemonics/params => ::m.c.mnemonics/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.mnemonics/id id)
                            (assoc :xt/id id))]
    (log/debug :create-record/starting {:params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/finer :create-record/finished {:id id})
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.mnemonics/item)]
  (map read-record (index-ids)))