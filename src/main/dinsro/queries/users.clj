(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
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

;; [../joins/users.cljc]

(>defn read-record
  [user-id]
  [uuid? => (? ::m.users/item)]
  (let [db     (c.xtdb/main-db)
        query  '{:find  [(pull ?id [*])]
                 :in    [[?id]]
                 :where [[?id ::m.users/id ?name]]}
        result (xt/q db query [user-id])
        record (ffirst result)]
    (when (get record ::m.users/id)
      (dissoc record :xt/id))))

(>defn read-records
  [ids]
  [(s/coll-of :xt/id) => (s/coll-of ::m.users/item)]
  (map read-record ids))

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
  (log/info :find-by-transaction/starting {:transaction-id transaction-id})
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
    (throw (ex-info "User already exists" {}))))

(def pk '?user-id)
(def ident-key ::m.users/id)
(def index-param-syms ['?account-id])

(>def ::query-params (s/keys))

(defn get-index-params
  [query-params]
  (let [{account-id ::m.accounts/id} query-params]
    [account-id]))

(defn get-index-query
  [query-params]
  (let [[account-id] (get-index-params query-params)]
    {:find  [pk]
     :in    [index-param-syms]
     :where (->> [['?user-id ident-key '_]]
                 (concat (when account-id
                           [['?account-id ::m.accounts/user '?user-id]]))
                 (filter identity)
                 (into []))}))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [::query-params => number?]
   (do
     (log/debug :count-ids/starting {:query-params query-params})
     (let [pk '?user-id
           base-params  (get-index-query query-params)
           limit-params {:find [(list 'count pk)]}
           params       (get-index-params query-params)
           query        (merge base-params limit-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-one query params)]
         (log/info :count-ids/finished {:n n})
         (or n 0))))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.users/id)]
   (index-ids {}))
  ([query-params]
   [::query-params => (s/coll-of ::m.users/id)]
   (do
     (log/debug :index-ids/starting {})
     (let [{:indexed-access/keys [options]}               query-params
           {:keys [limit offset] :or {limit 20 offset 0}} options
           base-params                                    (get-index-query query-params)
           limit-params                                   {:limit limit :offset offset}
           query                                          (merge base-params limit-params)
           params                                         (get-index-params query-params)]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-many query params)]
         (log/info :index-ids/finished {:ids ids})
         ids)))))

(>defn index-records
  "list all users"
  []
  [=> (s/coll-of ::m.users/item)]
  (read-records (index-ids)))

(>defn delete!
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
