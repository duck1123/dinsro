(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]
   [taoensso.timbre :as log]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record
  (let [user           (mocks/mock-user)
        user-id        (::m.users/id user)
        currency       (mocks/mock-currency)
        currency-id    (::m.currencies/id currency)
        params         (ds/gen-key m.accounts/required-params)
        params         (-> params
                           (assoc ::m.accounts/user user-id)
                           (assoc ::m.accounts/currency currency-id))
        id             (q.accounts/create-record params)
        created-record (q.accounts/read-record id)]
    (assertions
     (get params ::m.accounts/name) => (get created-record ::m.accounts/name))))

(deftest find-by-currency
  (let [user (mocks/mock-user)
        user-id (::m.users/id user)
        currency (mocks/mock-currency)
        currency-id (::m.currencies/id currency)
        account (mocks/mock-account user-id currency-id)
        account-id (::m.accounts/id account)]
    (assertions
     "should retutrn the matching account id"
     (q.accounts/find-by-currency currency-id) => [account-id])))

(deftest index-records
  (assertions
   (q.accounts/index-records) => []))

(deftest index-records-by-user-not-found
  (let [user-id (ds/gen-key uuid?)]
    (assertions
     (q.accounts/index-records-by-user user-id) => [])))

(deftest index-records-by-user-found
  (let [record  (mocks/mock-account)
        user-id (::m.accounts/user record)
        eid     (q.users/find-eid-by-id user-id)]
    (assertions
     (q.accounts/index-records-by-user eid) => [record])))

(deftest read-record-not-found
  (let [id (ds/gen-key :db/id)]
    (is (nil? (q.accounts/read-record id)))))

(deftest read-record-found
  (let [record (mocks/mock-account)
        id     (::m.accounts/id record)
        eid    (q.accounts/find-eid-by-id id)]
    (assertions
     "Should return mocked account"
     (q.accounts/read-record eid) => record)))

(deftest delete-record-success
  (let [account (mocks/mock-account)
        id      (::m.accounts/id account)
        eid     (q.accounts/find-eid-by-id id)]
    (assertions
     "record should exist"
     (q.accounts/read-record eid) => account

     "should return nil"
     (q.accounts/delete-record eid) => nil

     "record should note exist"
     (q.accounts/read-record eid) => nil)))
