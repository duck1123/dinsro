(ns dinsro.queries.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
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
    (log/finer :create-record/finished {:id id})
    id))

(>defn find-by-hex
  [hex]
  [::m.n.pubkeys/hex => (? ::m.n.pubkeys/id)]
  (log/finer :find-by-hex/starting {:hex hex})
  (c.xtdb/query-id
   '{:find  [?id]
     :in    [[?hex]]
     :where [[?id ::m.n.pubkeys/hex ?hex]]}
   [hex]))

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
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.n.pubkeys/id _]]}))

(>defn delete!
  [id]
  [::m.n.pubkeys/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(defn update!
  [id data]
  (log/info :update!/starting {:id id :data data})
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn find-by-subscription
  [subscription-id]
  [::m.n.subscription-pubkeys/subscription => (s/coll-of ::m.n.subscription-pubkeys/id)]
  (log/info :find-by-subscription/starting {:subscription-id subscription-id})
  (c.xtdb/query-ids
   '{:find  [?pubkey-id]
     :in    [[?subscription-id]]
     :where [[?sp-id ::m.n.subscription-pubkeys/pubkey ?pubkey-id]
             [?sp-id ::m.n.subscription-pubkeys/subscription ?subscription-id]]}
   [subscription-id]))

(>defn find-contacts
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.n.pubkeys/id)]
  (log/finer :find-contacts/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-ids
   '{:find  [?target-id]
     :in    [[?pubkey-id]]
     :where [[?pc-id ::m.n.pubkey-contacts/actor ?pubkey-id]
             [?pc-id ::m.n.pubkey-contacts/target ?target-id]]}
   [pubkey-id]))

(>defn find-by-name
  [name]
  [::m.n.pubkeys/name => ::m.n.pubkeys/id]
  (log/finer :find-by-name/starting {:name name})
  (c.xtdb/query-ids
   '{:find  [?pubkey-id]
     :in    [[?name]]
     :where [[?pubkey-id ::m.n.pubkeys/name ?name]]}
   [name]))
