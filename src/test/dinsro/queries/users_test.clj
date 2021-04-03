(ns dinsro.queries.users-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-valid
  (let [{::m.users/keys [email]
         :as            params} (ds/gen-key ::m.users/params)
        id                      (q.users/create-record params)
        user                    (q.users/read-record id)]
    (is (= email (::m.users/email user)))))

(deftest create-record-invalid
  (let [params (ds/gen-key ::m.users/params)
        id     (q.users/create-record params)]
    (q.users/read-record id)
    ;; TODO: Throw a better error
    (is (thrown? RuntimeException (q.users/create-record params)))))

(deftest read-record
  (let [{::m.users/keys [email]
         :as            params} (ds/gen-key ::m.users/params)
        id                      (q.users/create-record params)
        response                (q.users/read-record id)]
    (is (= email (::m.users/email response)))))

(deftest read-record-missing
  (let [id       (ds/gen-key :db/id)
        response (q.users/read-record id)]
    (is (nil? response))))

(deftest read-records-empty
  (let [ids      []
        response (q.users/read-records ids)]
    (is (= [] response))))

(deftest read-records-existing
  (let [n 2]
    (dotimes [_ n] (mocks/mock-user))
    (let [records  (q.users/index-records)
          ids      (map :db/id records)
          response (q.users/read-records ids)]
      (is (= records response)))))
