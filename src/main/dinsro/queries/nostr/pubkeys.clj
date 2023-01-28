(ns dinsro.queries.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]

(>defn create-record
  [params]
  [::m.n.pubkeys/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ::m.n.pubkeys/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn register-pubkey
  [hex]
  [::m.n.pubkeys/hex => ::m.n.pubkeys/id]
  (create-record {::m.n.pubkeys/hex hex}))

(>defn read-record
  [id]
  [:xt/id => (? ::m.n.pubkeys/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.pubkeys/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.pubkeys/id)]
  (let [db    (c.xtdb/main-db)
        query '[:find ?e :where [?e ::m.n.pubkeys/id _]]]
    (map first (xt/q db query))))

(>defn delete!
  [id]
  [::m.n.pubkeys/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))
