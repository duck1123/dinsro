(ns dinsro.queries.core-nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn create-record
  [params]
  [::m.core-nodes/params => :db/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.core-nodes/id id)
                            (assoc :crux.db/id id))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.core-nodes/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.core-nodes/name)
      (dissoc record :db/id))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-nodes/name _]]}]
    (map first (crux/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-nodes/item)]
  (map read-record (index-ids)))

(defn update-blockchain-info
  [id props]
  (let [node   (c.crux/main-node)
        db     (c.crux/main-db)
        old    (crux/pull db '[*] id)
        params (merge  old props)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))))

(defn update-wallet-info
  [{:keys               [balance tx-count]
    ::m.core-nodes/keys [id]}]
  (let [node   (c.crux/main-node)
        db     (c.crux/main-db)
        old    (crux/pull db '[*] id)
        params (merge
                old
                {:wallet-info/balance  balance
                 :wallet-info/tx-count tx-count})]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))))
