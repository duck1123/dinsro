(ns dinsro.actions.currencies-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.currencies :as a.currencies]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.currencies :as s.a.currencies]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]))

(def example-request {:name "foo"})

(use-fixtures
  :each
  (fn [f]
    (start-db f [m.currencies/schema])))

(deftest index-handler-test-success
  (let [request {}
        response (a.currencies/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [] items))))

(deftest index-handler-test-no-records
  (let [record (mocks/mock-currency)
        request {}
        response (a.currencies/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [record] items))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.currencies/create-request-valid)
        response (a.currencies/create-handler request)
        id (get-in response [:body :item :db/id])
        created-record (q.currencies/read-record id)]
    (is (not (nil? created-record))
        "record can be read")
    (is (= status/ok (:status response)))
    (is (= (:name request) (::m.currencies/name response)))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.currencies/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest delete-handler
  (let [currency (mocks/mock-currency)
        id (:db/id currency)
        request {:path-params {:id (str id)}}]
    (is (not (nil? (q.currencies/read-record id))))
    (let [response (a.currencies/delete-handler request)]
      (is (= status/ok (:status response)))
      (is (nil? (q.currencies/read-record id))))))

(deftest read-handler-success
  (let [currency (mocks/mock-currency)
        id (str (:db/id currency))
        request {:path-params {:id id}}
        response (a.currencies/read-handler request)]
    (is (= status/ok (:status response)))
    (is (= currency (get-in response [:body :item])))))

(deftest read-handler-not-found
  (let [request (ds/gen-key ::ds/common-read-request)
        response (a.currencies/read-handler request)]
    (is (= status/not-found (:status response)) "Returns a not-found status")
    (is (= :not-found (get-in response [:body :status])) "Has a not found status field")))
