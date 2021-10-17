(ns dinsro.queries.ln-nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln-nodes/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln-nodes/name _]]}]
    (map first (crux/q db query))))

(>defn read-record
  [id]
  [:db/id => (? ::m.ln-nodes/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.ln-nodes/name)
      (dissoc record :crux.db/id))))

(>defn create-record
  [params]
  [::m.ln-nodes/params => ::m.ln-nodes/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.ln-nodes/id id)
                            (assoc :crux.db/id id))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln-nodes/item)]
  (map read-record (index-ids)))

(defn create-transaction
  [params]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc :m.ln-tx/id id)
                            (assoc :crux.db/id id))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    id))

(>defn find-ids-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.ln-nodes/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?node-id]
                :in    [?user-id]
                :where [[?node-id ::m.ln-nodes/user ?user-id]]}]
    (map first (crux/q db query user-id))))

(>defn find-id-by-user-and-name
  [user-id name]
  [::m.users/id ::m.ln-nodes/name => (? ::m.ln-nodes/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?node-id]
                :in    [?user-id ?name]
                :where [[?node-id ::m.ln-nodes/user ?user-id]
                        [?node-id ::m.ln-nodes/name ?name]]}]
    (ffirst (crux/q db query user-id name))))
