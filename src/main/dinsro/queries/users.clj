(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.user-pubkeys :as m.n.user-pubkeys]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../joins/users.cljc]]

(def model-key ::m.users/id)

(def query-info
  {:ident   model-key
   :pk      '?user-id
   :clauses [[:actor/id       '?actor-id]
             [:actor/admin?   '?admin?]
             [::m.accounts/id '?account-id]]
   :sort-columns
   {::m.users/name '?user-name}
   :rules
   (fn [[actor-id admin? account-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?user-id ::m.users/id '?actor-id]])
          (concat-when account-id
            [['?account-id ::m.accounts/user '?user-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [uuid? => (? ::m.users/item)]
  (c.xtdb/read model-key id))

(>defn find-by-name
  [name]
  [::m.users/name => (? ::m.users/id)]
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?name]]
     :where [[?id ::m.users/name ?name]]}
   [name]))

(>defn find-by-pubkey
  [hex]
  [::m.n.pubkeys/hex => (s/coll-of ::m.users/id)]
  (log/info :find-by-pubkey/starting {:hex hex})
  (c.xtdb/query-values
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
  (c.xtdb/query-values
   '{:find  [?user-id]
     :in    [[?pubkey-id]]
     :where [[?uk-id ::m.n.user-pubkeys/pubkey ?pubkey-id]
             [?uk-id ::m.n.user-pubkeys/user ?user-id]]}
   [pubkey-id]))

(>defn find-by-transaction
  [transaction-id]
  [::m.transactions/id => (? ::m.users/id)]
  (log/info :find-by-transaction/starting {:transaction-id transaction-id})
  (c.xtdb/query-value
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
    (let [node   (c.xtdb/get-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.users/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (ex-info "User already exists" {}))))

(>defn delete!
  "delete user by id"
  [id]
  [:xt/id => nil?]
  (c.xtdb/delete! id))

(>defn update!
  [id data]
  [::m.users/id (s/keys) => ::m.users/id]
  (log/info :update!/starting {:id id :data data})
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))
