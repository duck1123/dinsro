(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]))

(def schemata [m.users/schema
               m.accounts/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record
  (let [params         (ds/gen-key ::m.accounts/params)
        id             (q.accounts/create-record params)
        created-record (q.accounts/read-record id)]
    (is (= (get params ::m.accounts/name) (get created-record ::m.accounts/name)))))

(deftest index-records
  (is (= [] (q.accounts/index-records))))

(deftest index-records-by-user
  (let [user-id 1]
    (is (= [] (q.accounts/index-records-by-user user-id)))))

(deftest index-records-by-user-found
  (let [record  (mocks/mock-account)
        user-id (get-in record [::m.accounts/user :db/id])]
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
