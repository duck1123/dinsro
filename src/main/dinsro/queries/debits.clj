(ns dinsro.queries.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [../../../notebooks/dinsro/notebooks/debits.clj]
;; [../actions/debits.clj]
;; [../joins/debits.cljc]
;; [../model/debits.cljc]

(def query-info
  "Query info for Debits"
  {:ident        ::m.debits/id
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
  (let [id     (new-uuid)
        node   (c.xtdb/get-node)
        params (assoc params ::m.debits/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.debits/id => (? ::m.debits/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.debits/id)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.debits/id => nil?]
  (c.xtdb/delete! id))

(>defn find-by-account
  [account-id]
  [::m.accounts/id => (s/coll-of ::m.debits/id)]
  (log/info :find-by-account/starting {:account-id account-id})
  (c.xtdb/query-values
   '{:find  [?debit-id]
     :in    [[?account-id]]
     :where [[?debit-id ::m.debits/account ?account-id]]}
   [account-id]))

(>defn find-by-transaction
  [transaction-id]
  [::m.debits/transaction => (s/coll-of ::m.debits/id)]
  (log/info :find-by-transaction/starting {:transaction-id transaction-id})
  (c.xtdb/query-values
   '{:find  [?debit-id]
     :in    [[?transaction-id]]
     :where [[?debit-id ::m.debits/transaction ?transaction-id]]}
   [transaction-id]))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.debits/id)]
  (log/info :find-by-user/starting {:user-id user-id})
  (c.xtdb/query-values
   '{:find  [?debit-id]
     :in    [[?transaction-id]]
     :where [[?debit-id ::m.debits/account ?account-id]
             [?account-id ::m.accounts/user ?user-id]]}
   [user-id]))
