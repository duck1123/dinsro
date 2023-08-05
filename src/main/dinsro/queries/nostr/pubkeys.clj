(ns dinsro.queries.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/pubkeys.clj]]
;; [[../../model/nostr/pubkeys.cljc]]

(def model-key ::m.n.pubkeys/id)

(def query-info
  {:ident   model-key
   :pk      '?pubkey-id
   :clauses [[::m.n.pubkeys/hex '?pubkey-hex]]
   :rules
   (fn [[pubkey-hex] rules]
     (->> rules
          (concat-when pubkey-hex
            ['?pubkey-id ::m.n.pubkeys/hex '?pubkey-hex])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.n.pubkeys/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.pubkeys/id => nil?]
  (c.xtdb/delete! id))

(>defn create-record
  [params]
  [::m.n.pubkeys/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn find-by-hex
  [hex]
  [::m.n.pubkeys/hex => (? ::m.n.pubkeys/id)]
  (log/trace :find-by-hex/starting {:hex hex})
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?hex]]
     :where [[?id ::m.n.pubkeys/hex ?hex]]}
   [hex]))

(>defn register-pubkey
  [hex]
  [::m.n.pubkeys/hex => ::m.n.pubkeys/id]
  (create-record {::m.n.pubkeys/hex hex}))

(defn update!
  [id data]
  (log/info :update!/starting {:id id :data data})
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn find-contacts
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.n.pubkeys/id)]
  (log/trace :find-contacts/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-values
   '{:find  [?target-id]
     :in    [[?pubkey-id]]
     :where [[?pc-id ::m.n.pubkey-contacts/actor ?pubkey-id]
             [?pc-id ::m.n.pubkey-contacts/target ?target-id]]}
   [pubkey-id]))

(>defn find-by-name
  [name]
  [::m.n.pubkeys/name => ::m.n.pubkeys/id]
  (log/trace :find-by-name/starting {:name name})
  (c.xtdb/query-values
   '{:find  [?pubkey-id]
     :in    [[?name]]
     :where [[?pubkey-id ::m.n.pubkeys/name ?name]]}
   [name]))
