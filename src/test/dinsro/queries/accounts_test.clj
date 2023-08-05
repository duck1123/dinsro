(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record
  (let [user           (mocks/mock-user)
        user-id        (::m.users/id user)
        currency       (mocks/mock-currency)
        currency-id    (::m.currencies/id currency)
        source         (mocks/mock-rate-source currency-id)
        source-id      (::m.rate-sources/id source)
        params         (ds/gen-key m.accounts/required-params)
        params         (-> params
                           (assoc ::m.accounts/currency currency-id)
                           (assoc ::m.accounts/source source-id)
                           (assoc ::m.accounts/user user-id))
        id             (q.accounts/create-record params)
        created-record (q.accounts/read-record id)]
    (assertions
     (get params ::m.accounts/name) => (get created-record ::m.accounts/name))))

(deftest find-by-currency
  (let [user        (mocks/mock-user)
        user-id     (::m.users/id user)
        currency    (mocks/mock-currency)
        currency-id (::m.currencies/id currency)
        source      (mocks/mock-rate-source currency-id)
        source-id   (::m.rate-sources/id source)
        account     (mocks/mock-account user-id currency-id source-id)
        account-id  (::m.accounts/id account)]
    (assertions
     "should retutrn the matching account id"
     (q.accounts/find-by-currency currency-id) => [account-id])))

(deftest read-record-not-found
  (let [id (ds/gen-key ::m.accounts/id)]
    (is (nil? (q.accounts/read-record id)))))

(deftest read-record-found
  (let [record (mocks/mock-account)
        id     (::m.accounts/id record)]
    (assertions
     "Should return mocked account"
     (q.accounts/read-record id) => record)))

(deftest delete!-success
  (let [account (mocks/mock-account)
        id      (::m.accounts/id account)]
    (assertions
     "record should exist"
     (q.accounts/read-record id) => account

     "should return nil"
     (q.accounts/delete! id) =fn=> (comp not nil?)

     "record should note exist"
     (q.accounts/read-record id) => nil)))
