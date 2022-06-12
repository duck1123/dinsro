(ns dinsro.queries.core.networks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.c.networks/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.networks/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.c.networks/id => (? ::m.c.networks/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.networks/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.networks/id _]]}]
    (map first (xt/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.networks/item)]
  (map read-record (index-ids)))

(>defn find-id-by-name
  [network-name]
  [::m.c.networks/name => (? ::m.c.networks/id)]
  (log/finer :find-id-by-name/starting {:network-name network-name})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?network-id]
                :in    [?network-name]
                :where [[?network-id ::m.c.networks/name ?network-name]]}
        id (ffirst (xt/q db query network-name))]
    (log/info :find-id-by-name/finished {:id id :network-name network-name})
    id))

(defn find-by-chain-and-network
  [chain-name network-name]
  (log/finer :find-by-chain-and-network/starting
             {:chain-name chain-name :network-name network-name})
  (let [db     (c.xtdb/main-db)
        query  '{:find  [?network-id]
                 :in    [[?chain-name ?network-name]]
                 :where [[?chain-id ::m.c.chains/name ?chain-name]
                         [?network-id ::m.c.networks/chain ?chain-id]
                         [?network-id ::m.c.networks/name ?network-name]]}
        id (ffirst (xt/q db query [chain-name network-name]))]
    (log/info :find-id-by-name/finished
              {:chain-name chain-name :network-name network-name :id id})
    id))

(>defn delete!
  [id]
  [::m.c.networks/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))
