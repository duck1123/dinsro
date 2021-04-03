(ns dinsro.components.transaction-value-chart-test
  (:require
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.transaction-value-chart :as c.transaction-value-chart]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(let [data [[1  1]
            [2  2]
            [3  4]
            [4  8]
            [5 16]]]
  (defcard data data)

  (let [account (ds/gen-key ::s.accounts/item)
        account-id (::m.accounts/id account)]
    (defcard account account)

    (let [currency-id (::m.currencies/id (::s.accounts/currency account))
          currency (and currency-id (assoc (ds/gen-key ::s.currencies/item)
                                           ::m.currencies/id currency-id))]
      (defcard currency currency)

      (let [transactions (map
                          (fn [transaction]
                            (assoc-in transaction [::s.transactions/account ::m.accounts/id] account-id))
                          (ds/gen-key (s/coll-of ::s.transactions/item)))]
        (defcard transactions transactions)

        (let [new-data (map
                        (fn [transaction]
                          [(::s.transactions/date transaction)
                           (::s.transactions/value transaction)])
                        transactions)]
          (defcard new-data new-data)

         (defcard-rg transaction-value-chart
           [c.transaction-value-chart/transaction-value-chart account new-data]))))))
