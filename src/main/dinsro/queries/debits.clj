(ns dinsro.queries.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [lambdaisland.glogc :as log]))

;; [[../actions/debits.clj]]
;; [[../joins/debits.cljc]]
;; [[../model/debits.cljc]]
;; [[../../../notebooks/dinsro/notebooks/debits.clj]]

(def model-key ::m.debits/id)

(def query-info
  "Query info for Debits"
  {:ident        model-key
   :pk           '?debit-id
   :clauses      [[:actor/id           '?actor-id]
                  [:actor/admin?       '?admin?]
                  [::m.accounts/id     '?account-id]
                  [::m.transactions/id '?transaction-id]
                  [::m.currencies/id   '?currency-id]
                  [:positive?          '?positive]]
   :sort-columns {::m.debits/value   '?value
                  ::m.debits/account '?account}
   :rules
   (fn [[actor-id admin? account-id transaction-id currency-id positive?] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?debit-id            ::m.debits/account     '?auth-account-id]
             ['?auth-account-id     ::m.accounts/user      '?actor-id]])
          (concat-when account-id
            [['?debit-id            ::m.debits/account     '?account-id]])
          (concat-when transaction-id
            [['?debit-id            ::m.debits/transaction '?transaction-id]])
          (concat-when currency-id
            [['?debit-id            ::m.debits/account     '?currency-account-id]
             ['?currency-account-id ::m.accounts/currency  '?currency-id]])
          (concat-when (and (not (nil? positive?)) positive?)
            [['?debit-id            ::m.debits/value       '?value]
             ['(pos? ?value)]])
          (concat-when (and (not (nil? positive?)) (not positive?))
            [['?debit-id            ::m.debits/value       '?value]
             ['(neg? ?value)]])))})

(defn count-ids
  "Count debit records"
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  "Index debit records"
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  "Create a debit record"
  [params]
  [::m.debits/params => ::m.debits/id]
  (log/info :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.debits/id => (? ::m.debits/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.debits/id => nil?]
  (c.xtdb/delete! id))

(>defn find-by-transaction
  [transaction-id]
  [::m.debits/transaction => (s/coll-of ::m.debits/id)]
  (log/info :find-by-transaction/starting {:transaction-id transaction-id})
  (c.xtdb/query-values
   '{:find  [?debit-id]
     :in    [[?transaction-id]]
     :where [[?debit-id ::m.debits/transaction ?transaction-id]]}
   [transaction-id]))
