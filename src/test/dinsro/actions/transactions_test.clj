(ns dinsro.actions.transactions-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.transactions :as a.transactions]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.transactions :as s.a.transactions]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]
   [tick.alpha.api :as tick]))

(use-fixtures
  :each
  (fn [f]
    (start-db f [m.users/schema
                 m.currencies/schema
                 m.transactions/schema])))

(deftest prepare-record
  (let [account-id 1
        description "foo"
        value 1
        date (tick/instant)
        params {:account-id account-id
                :description description
                :date (str date)
                :value value}
        response (a.transactions/prepare-record params)
        expected {::m.transactions/date date
                  ::m.transactions/description description
                  ::m.transactions/value (double value)
                  ::m.transactions/account {:db/id account-id}}]
    (is (= expected response)
        "Returns all params in the expected format")))

(deftest create-record-response-test
  (let [request (ds/gen-key s.a.transactions/create-request-valid)
        response (a.transactions/create-handler request)]
    (is (= (:status response) status/ok)
        "returns ok status")))

(deftest index-handler-empty
  (let [request {}
        response (a.transactions/index-handler request)]
    (is (= (:status response) status/ok)
        "returns ok status")))

(deftest index-handler-with-records
  (mocks/mock-transaction)
  (let [request {}
        response (a.transactions/index-handler request)]
    (is (= (:status response) status/ok)
        "returns ok status")
    (is (= 1 (count (:items (:body response))))
        "returns only a single record")))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.transactions/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest delete-handler-success
  (let [item (mocks/mock-transaction)
        id (:db/id item)
        request {:path-params {:id (str id)}}]
    (is (not (nil? (q.transactions/read-record id))))
    (let [response (a.transactions/delete-handler request)]
      (is (= status/ok (:status response))
          "returns ok status")
      (is (nil? (q.transactions/read-record id))
          "record can no longer be found"))))

(deftest read-handler-success
  (let [item (mocks/mock-transaction)
        id (str (:db/id item))
        request {:path-params {:id id}}
        response (a.transactions/read-handler request)]
    (is (= status/ok (:status response))
        "returns ok status")
    (is (= item (get-in response [:body :item]))
        "returns item")))

(deftest read-handler-not-found
  (let [request (ds/gen-key ::ds/common-read-request)
        response (a.transactions/read-handler request)]
    (is (= status/not-found (:status response))
        "Returns a not-found status")

    (is (= :not-found (get-in response [:body :status]))
        "Has a not found status field")))
