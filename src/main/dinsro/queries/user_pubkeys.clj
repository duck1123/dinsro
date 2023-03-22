(ns dinsro.queries.user-pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.user-pubkeys :as m.n.user-pubkeys]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../actions/users.clj][User Actions]]
;; [[../model/users.cljc][Users Model]]
;; [[../ui/users.cljs][Users UI]]

(>defn read-record
  [user-id]
  [uuid? => (? ::m.users/item)]
  (let [db     (c.xtdb/main-db)
        query  '{:find  [(pull ?id [*])] :in    [[?id]]}
        result (xt/q db query [user-id])
        record (ffirst result)]
    (when (get record ::m.users/id)
      (dissoc record :xt/id))))

(>defn find-by-name
  [name]
  [::m.users/name => (? ::m.users/id)]
  (c.xtdb/query-id
   '{:find  [?id]
     :in    [[?name]]
     :where [[?id ::m.users/name ?name]]}
   [name]))

(>defn find-by-pubkey
  [hex]
  [::m.n.pubkeys/hex => (s/coll-of ::m.users/id)]
  (log/info :find-by-pubkey/starting {:hex hex})
  (c.xtdb/query-ids
   '{:find  [?user-id]
     :in    [[?hex]]
     :where [[?pubkey-id ::m.n.pubkeys/hex ?hex]
             [?uk-id ::m.n.user-pubkeys/pubkey ?pubkey-id]
             [?uk-id ::m.n.user-pubkeys/user ?user-id]]}
   [hex]))

(>defn find-by-pubkey-id
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.users/id)]
  (log/info :find-by-pubkey/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-ids
   '{:find  [?user-id]
     :in    [[?pubkey-id]]
     :where [[?uk-id ::m.n.user-pubkeys/pubkey ?pubkey-id]
             [?uk-id ::m.n.user-pubkeys/user ?user-id]]}
   [pubkey-id]))

(>defn find-by-transaction
  [transaction-id]
  [::m.transactions/id => (? ::m.users/id)]
  (c.xtdb/query-id
   '{:find  [?user-id]
     :in    [[?transaction-id]]
     :where [[?transaction-id ::m.transactions/account ?account-id]
             [?account-id ::m.accounts/user ?user-id]]}
   [transaction-id]))

(>defn create-record
  "Create a user record"
  [params]
  [::m.users/params => ::m.users/id]
  (if (nil? (find-by-name (::m.users/name params)))
    (let [node   (c.xtdb/main-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.users/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  "list all user ids"
  []
  [=> (s/coll-of ::m.users/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.users/id _]]}))

(>defn delete-record
  "delete user by id"
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn update!
  [id data]
  [::m.users/id (s/keys) => ::m.users/id]
  (log/info :update!/starting {:id id :data data})
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))
