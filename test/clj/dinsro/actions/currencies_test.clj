(ns dinsro.actions.currencies-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
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
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      ;; (d/transact db/*conn* m.users/schema)
      (d/transact db/*conn* s.currencies/schema)
      (f))))

(defn gen-spec
  [spec]
  (gen/generate (s/gen spec)))

(deftest index-handler-test
  (testing "success - no records"
    (let [request {}
          response (a.currencies/index-handler request)
          items (get-in response [:body :items])]
      (is (= status/ok (:status response)))
      (is (= [] items))))
  (testing "success - with records"
    (let [record (m.currencies/mock-record)
          request {}
          response (a.currencies/index-handler request)
          items (get-in response [:body :items])]
      (is (= status/ok (:status response)))
      (is (= [record] items)))))

(deftest create-handler
  (testing "success"
    (let [request (gen-spec ::a.currencies/create-handler-request-valid)
          response (a.currencies/create-handler request)
          id (get-in response [:body :item :db/id])
          created-record (m.currencies/read-record id)]
      (is (= status/ok (:status response)))
      (is (= (:name request) (::s.currencies/name response)))))
  (testing "invalid params"
    (let [params {}
          request {:params params}
          response (a.currencies/create-handler request)]
      (is (= status/bad-request (:status response))
          "should signal a bad request"))))

(deftest delete-handler
  (testing "success"
    (let [currency (m.currencies/mock-record)
          id (:db/id currency)
          request {:path-params {:id (str id)}}]
      (is (not (nil? (m.currencies/read-record id))))
      (let [response (a.currencies/delete-handler request)]
        (is (= status/ok (:status response)))
        (is (nil? (m.currencies/read-record id)))))))

(deftest read-handler-success
  (let [currency (m.currencies/mock-record)
        id (str (:db/id currency))
        request {:path-params {:id id}}
        response (a.currencies/read-handler request)]
    (is (= status/ok (:status response)))
    (is (= currency (get-in response [:body :item])))))

(deftest read-handler-not-found
  (let [id (gen/generate (s/gen :read-currency-request-path-params/id))
        request {:path-params {:id id}}
        response (a.currencies/read-handler request)]
    (is (= status/not-found (:status response)) "Returns a not-found status")
    (is (= :not-found (get-in response [:body :status])) "Has a not found status field")))

(comment
  (gen/generate (s/gen :read-currency-request-path-params/id))
  (gen-spec ::a.currencies/create-handler-request)

  (gen-spec :create-handler-valid/request)


  )
