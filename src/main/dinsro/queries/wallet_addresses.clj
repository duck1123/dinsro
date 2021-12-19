(ns dinsro.queries.wallet-addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.wallet-addresses/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.wallet-addresses/name _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.wallet-addresses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.wallet-addresses/name)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.wallet-addresses/params => ::m.wallet-addresses/id]
  (let [node            (c.xtdb/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.wallet-addresses/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.wallet-addresses/item)]
  (map read-record (index-ids)))
