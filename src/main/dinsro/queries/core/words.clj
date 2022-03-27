(ns dinsro.queries.core.words
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.words/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.words/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.c.words/id => (? ::m.c.words/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.words/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.words/params => ::m.c.words/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.words/id id)
                            (assoc :xt/id id))]
    (log/debug :word/create {:params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/debug :word/created {:id id})
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.wallets/item)]
  (map read-record (index-ids)))

(>defn find-by-wallet
  [wallet-id]
  [::m.c.wallets/id => (s/coll-of ::m.c.words/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?word-id]
                :in    [?wallet-id]
                :where [[?word-id ::m.c.words/wallet ?wallet-id]]}]
    (map first (xt/q db query wallet-id))))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))
