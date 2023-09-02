(ns dinsro.queries.transactions
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >defn ?]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [tick.alpha.api :as t]
   [xtdb.api :as xt]))

;; [[../actions/transactions.clj]]
;; [[../joins/transactions.cljc]]
;; [[../model/transactions.cljc]]
;; [[../ui/transactions.cljs]]

(def model-key ::m.transactions/id)

(def query-info
  {:ident   model-key
   :pk      '?transaction-id
   :clauses [[:actor/id         '?actor-id]
             [:actor/admin?     '?admin?]
             [::m.users/id      '?user-id]
             [::m.accounts/id   '?account-id]
             [::m.currencies/id '?currency-id]
             [::m.categories/id '?category-id]]
   :order-by [['?date :desc]]
   :sort-columns
   {::m.transactions/date '?sort-date}
   :rules
   (fn [[actor-id admin? user-id account-id currency-id category-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?auth-debit-id    ::m.debits/transaction    '?transaction-id]
             ['?auth-debit-id    ::m.debits/account        '?auth-account-id]
             ['?auth-account-id  ::m.accounts/user         '?actor-id]])
          (concat-when account-id
            [['?account-debit-id ::m.debits/transaction    '?transaction-id]
             ['?account-debit-id ::m.debits/account        '?account-id]])
          (concat-when user-id
            [['?user-account-id  ::m.accounts/user         '?user-id]
             ['?user-debit-id    ::m.debits/account        '?user-account-id]
             ['?user-debit-id    ::m.debits/transaction    '?transaction-id]])
          (concat-when currency-id
            [['?transaction-id   ::m.transactions/currency '?currency-id]])
          (concat-when category-id
            ['?transaction-id    ::m.transactions/category '?category-id])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.transactions/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.transactions/id id)
                            (assoc :xt/id id)
                            (update ::m.transactions/date t/inst))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.transactions/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.transactions/id)
      (-> record
          (update ::m.transactions/date t/instant)
          (dissoc :xt/id)))))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (log/info :delete!/starting {:id id})
  (c.xtdb/delete! id))
