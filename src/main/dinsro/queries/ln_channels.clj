(ns dinsro.queries.ln-channels
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.ln-channels :as m.ln-channels]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln-channels/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln-channels/id _]]}]
    (map first (crux/q db query))))

(>defn read-record
  [id]
  [:db/id => (? ::m.ln-channels/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.ln-channels/id)
      (dissoc record :crux.db/id))))

(>defn create-record
  [params]
  [::m.ln-channels/params => ::m.ln-channels/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.ln-channels/id id)
                            (assoc :crux.db/id id))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln-channels/item)]
  (map read-record (index-ids)))

(defn find-channel
  [_node-id _channel-point]
  nil)
