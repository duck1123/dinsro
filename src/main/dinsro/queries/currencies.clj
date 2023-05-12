(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [io.pedestal.log :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.currencies/id
   :pk      '?currencies-id
   :clauses [[::m.users/id '?user-id]]
   :rules
   (fn [[user-id] rules]
     (->> rules
          (concat-when user-id
            [['?category-id ::m.currencies/user '?user-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(def record-limit 1000)

(>defn find-by-code
  [code]
  [::m.currencies/code => ::m.currencies/id]
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?code]]
     :where [[?id ::m.currencies/code ?code]]}
   [code]))

(>defn find-by-debit
  [debit-id]
  [::m.debits/id => (? ::m.currencies/id)]
  (c.xtdb/query-value
   '{:find  [?currency-id]
     :in    [[?debit-id]]
     :where [[?debit-id ::m.debits/account ?account-id]
             [?account-id ::m.accounts/currency ?currency-id]]}
   [debit-id]))

(>defn find-by-user-and-name
  [user-id name]
  [::m.users/id ::m.currencies/name => (? ::m.currencies/id)]
  (c.xtdb/query-values
   '{:find  [?currency-id]
     :in    [[?user-id ?name]]
     :where [[?currency-id ::m.currencies/user ?user-id]
             [?currency-id ::m.currencies/name ?name]]}
   [user-id name]))

(>defn create-record
  [params]
  [::m.currencies/params => (? ::m.currencies/id)]
  (try
    (let [node   (c.xtdb/get-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.currencies/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (catch Exception ex
      (log/error :create/failed {:exception ex})
      nil)))

(>defn read-record
  [id]
  [::m.currencies/id => (? ::m.currencies/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.currencies/id)
      (dissoc record :xt/id))))

(>defn index-records
  []
  [=> (s/coll-of ::m.currencies/item)]
  (map read-record (index-ids)))

(>defn delete!
  "Delete a currency"
  [id]
  [:xt/id => nil?]
  (log/info :delete!/starting {:id id})
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    (log/info :delete!/finished {:id id})
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))
