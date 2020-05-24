(ns dinsro.actions.categories-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.categories :as a.categories]
   [dinsro.mocks :as mocks]
   [dinsro.model.categories :as m.categories]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.categories :as s.a.categories]
   [dinsro.spec.categories :as s.categories]
   [dinsro.spec.users :as s.users]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]))

(def example-request {:name "foo"})

(use-fixtures
  :each
  (fn [f]
    (start-db f [s.users/schema s.categories/schema])))

(deftest index-handler-test-success
  (let [request {}
        response (a.categories/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [] items))))

(deftest index-handler-test-no-records
  (let [record (mocks/mock-category)
        request {}
        response (a.categories/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [record] items))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.categories/create-request-valid)
        response (a.categories/create-handler request)
        id (get-in response [:body :item :db/id])
        created-record (m.categories/read-record id)]
    (is (not (nil? created-record))
        "record can be read")
    (is (= status/ok (:status response)))
    (is (= (:name request) (::s.categories/name response)))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.categories/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest delete-handler
  (let [currency (mocks/mock-category)
        id (:db/id currency)
        request {:path-params {:id (str id)}}]
    (is (not (nil? (m.categories/read-record id))))
    (let [response (a.categories/delete-handler request)]
      (is (= status/ok (:status response)))
      (is (nil? (m.categories/read-record id))))))

(deftest read-handler-success
  (let [category (mocks/mock-category)
        id (:db/id category)
        request {:path-params {:id (str id)}}
        response (a.categories/read-handler request)]
    (is (= status/ok (:status response)))
    (is (= category (get-in response [:body])))))

(deftest read-handler-not-found
  (let [request (ds/gen-key ::ds/common-read-request)
        response (a.categories/read-handler request)]
    (is (= status/not-found (:status response)) "Returns a not-found status")
    (is (= :not-found (get-in response [:body :status])) "Has a not found status field")))
