(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions behavior specification]]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema
   m.currencies/schema
   m.accounts/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "create-record"
  (let [user           (mocks/mock-user)
        user-id        (::m.users/id user)
        currency       (mocks/mock-currency)
        currency-id    (::m.currencies/id currency)
        params         (ds/gen-key m.accounts/required-params)
        params         (-> params
                           (assoc-in [::m.accounts/user ::m.users/id] user-id)
                           (assoc-in [::m.accounts/currency ::m.currencies/id] currency-id))
        id             (q.accounts/create-record params)
        created-record (q.accounts/read-record id)]
    (assertions
     (get params m.accounts/name) => (get created-record m.accounts/name))))

(specification "index-records"
  (assertions
   (q.accounts/index-records) => []))

(specification "index-records-by-user"
  (behavior "not found"
    (let [user-id 1]
      (assertions
       (q.accounts/index-records-by-user user-id) => [])))
  (behavior "found"
    (let [record  (mocks/mock-account)
          user-id (get-in record [m.accounts/user ::m.users/id])
          eid     (q.users/find-eid-by-id user-id)]
      (assertions
       (q.accounts/index-records-by-user eid) => [record]))))

(specification "read-record"
  (behavior "not found"
    (let [id (ds/gen-key :db/id)]
      (is (nil? (q.accounts/read-record id)))))
  (behavior "found"
    (let [record (mocks/mock-account)
          id     (::m.accounts/id record)
          eid    (q.accounts/find-eid-by-id id)]
      (assertions
       "Should return mocked account"
       (q.accounts/read-record eid) => record))))

(specification "delete-record"
  (behavior "success"
    (let [account (mocks/mock-account)
          id      (::m.accounts/id account)
          eid     (q.accounts/find-eid-by-id id)]
      (assertions
       "record should exist"
       (q.accounts/read-record eid) => account

       "should return nil"
       (q.accounts/delete-record eid) => nil

       "record should note exist"
       (q.accounts/read-record eid) => nil))))
