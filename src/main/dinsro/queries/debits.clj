(ns dinsro.queries.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../../notebooks/dinsro/notebooks/debits.clj]]
;; [[../model/debits.cljc][Model]]

(>defn create-record
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

(defn get-index-query
  [query-params]
  (let [transaction-id (::m.transactions/id query-params)
        positive?      (:positive? query-params)]
    {:find  (as-> ['?debit-id] x
              (concat x (when-not (nil? positive?) ['?value]))
              (filter identity x)
              (into [] x))
     :in    [['?transaction-id]]
     :where (->> [['?debit-id ::m.debits/id '_]]
                 (concat (when transaction-id
                           [['?debit-id ::m.debits/transaction '?transaction-id]]))
                 (concat (when (and (not (nil? positive?)) positive?)
                           [['?debit-id ::m.debits/value '?value]
                            ['(pos? ?value)]]))
                 (concat (when (and (not (nil? positive?)) (not positive?))
                           [['?debit-id ::m.debits/value '?value]
                            ['(neg? ?value)]]))
                 (filter identity)
                 (into []))}))

(defn get-index-params
  [query-params]
  (let [transaction-id (::m.transactions/id query-params)]
    [transaction-id]))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [any? => number?]
   (do
     (log/debug :count-ids/starting {:query-params query-params})
     (let [base-params  (get-index-query query-params)
           limit-params {:find ['(count ?witness-id)]}
           params       (get-index-params query-params)
           query        (merge base-params limit-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-value query params)]
         (log/info :count-ids/finished {:n n})
         (or n 0))))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.debits/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.debits/id)]
   (do
     (log/debug :index-ids/starting {})
     (let [{:indexed-access/keys [options]}                 query-params
           {:keys [limit options] :or {limit 20 options 0}} options
           base-params                                      (get-index-query query-params)
           limit-params                                     {:limit limit :options options}
           query                                            (merge base-params limit-params)
           params                                           (get-index-params query-params)]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-values query params)]
         (log/info :index-ids/finished {:ids ids})
         ids)))))

(>defn delete!
  [id]
  [::m.debits/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

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
