(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [io.pedestal.log :as log]))

(def model-key ::m.currencies/id)
(def record-limit 1000)

(def query-info
  {:ident   model-key
   :pk      '?currencies-id
   :clauses [[::m.users/id '?user-id]
             [::m.currencies/code '?code]
             [::m.currencies/name '?name]]
   :rules
   (fn [[user-id code name] rules]
     (->> rules
          (concat-when user-id
            [['?category-id ::m.currencies/user '?user-id]])
          (concat-when code
            [['?category-id ::m.currencies/code '?code]])
          (concat-when name
            [['?category-id ::m.currencies/name '?name]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

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

(>defn create-record
  [params]
  [::m.currencies/params => (? ::m.currencies/id)]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.currencies/id => (? ::m.currencies/item)]
  (c.xtdb/read model-key id))

(>defn index-records
  []
  [=> (s/coll-of ::m.currencies/item)]
  (map read-record (index-ids)))

(>defn delete!
  "Delete a currency"
  [id]
  [:xt/id => nil?]
  (log/info :delete!/starting {:id id})
  (c.xtdb/delete! id))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))
