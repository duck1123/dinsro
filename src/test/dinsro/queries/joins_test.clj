(ns dinsro.queries.joins-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest find-by-currency
  (let [user        (mocks/mock-user)
        user-id     (::m.users/id user)
        currency    (mocks/mock-currency)
        currency-id (::m.currencies/id currency)
        rate-source (mocks/mock-rate-source)
        source-id   (::m.rate-sources/id rate-source)
        account     (mocks/mock-account user-id currency-id source-id)
        account-id  (::m.accounts/id account)
        account2    (mocks/mock-account user-id currency-id source-id)
        account-id2 (::m.accounts/id account2)]

    ;; non-matching account
    (mocks/mock-account)

    (assertions
     "should retutrn the matching account id"
     (set (q.accounts/find-by-currency currency-id)) => #{account-id2 account-id})))
