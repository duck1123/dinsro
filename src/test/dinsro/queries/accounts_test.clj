(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions specification]]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema
   m.currencies/schema
   m.accounts/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "create-record"
  (let [user           (mocks/mock-user)
        user-id        (:db/id user)
        currency       (mocks/mock-currency)
        currency-id    (:db/id currency)
        params         (ds/gen-key m.accounts/required-params)
        params         (-> params
                           (assoc-in [::m.accounts/user :db/id] user-id)
                           (assoc-in [::m.accounts/currency :db/id] currency-id))
        id             (q.accounts/create-record params)
        created-record (q.accounts/read-record id)]

    (assertions
     (get params m.accounts/name) => (get created-record m.accounts/name))))

(specification "index-records"
  (assertions
   (q.accounts/index-records) => []))

(deftest index-records-by-user
  (let [user-id 1]
    (is (= [] (q.accounts/index-records-by-user user-id)))))

(deftest index-records-by-user-found
  (let [record  (mocks/mock-account)
        user-id (get-in record [m.accounts/user :db/id])]
    (is (= [record] (q.accounts/index-records-by-user user-id)))))

(deftest read-record-not-found
  (let [id (ds/gen-key :db/id)]
    (is (nil? (q.accounts/read-record id)))))

(deftest read-record-found
  (let [record (mocks/mock-account)
        id     (:db/id record)]
    (is (= record (q.accounts/read-record id)))))

(deftest delete-record
  (let [account (mocks/mock-account)
        id      (:db/id account)]
    (is (not (nil? (q.accounts/read-record id))))
    (let [response (q.accounts/delete-record id)]
      (is (nil? response))
      (is (nil? (q.accounts/read-record id))))))
