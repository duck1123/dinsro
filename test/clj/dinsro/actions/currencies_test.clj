(ns dinsro.actions.currencies-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.specs :as ds]
            [dinsro.spec.actions.currencies :as s.a.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def example-request {:name "foo"})

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      ;; (d/transact db/*conn* m.users/schema)
      (d/transact db/*conn* s.currencies/schema)
      (f))))

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
  (let [request (ds/gen-key ::s.a.currencies/create-handler-request-valid)
        response (a.currencies/create-handler request)
        id (get-in response [:body :item :db/id])
        created-record (m.currencies/read-record id)]
    (is (= status/ok (:status response)))
    (is (= (:name request) (::s.currencies/name response)))))

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
    (is (not (nil? (m.currencies/read-record id))))
    (let [response (a.currencies/delete-handler request)]
      (is (= status/ok (:status response)))
      (is (nil? (m.currencies/read-record id))))))

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
