(ns dinsro.actions.rates-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [datahike.api :as d]
            [datahike.config :as d.config]
            [dinsro.actions.rates :as a.rates]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.actions.rates :as s.a.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (d.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.rates/schema)
      (f))))

(deftest index-handler
  (testing "success"
    (let [request {}
          response (a.rates/index-handler request)]
      (is (= (:status response) status/ok))
      (let [body (:body response)
            items (:items body)]
        (is (= [] items)))
      #_(is (= true response)))))

(deftest create-handler-valid
  (testing "success"
    (let [request (gen/generate (s/gen ::s.a.rates/create-handler-request-valid))
          response (a.rates/create-handler request)]
      (is (= status/ok (:status response)))
      (let [id (get-in response [:body :item :db/id])]
        (is (not (nil? ident?)))
        (let [created-record (m.rates/read-record id)]
         (is (= (:name request) (::s.rates/name response))))))))

(deftest create-handler-invalid
  (testing "invalid params"
      (let [params {}
            request {:params params}
            response (a.rates/create-handler request)]
        (is (= status/bad-request (:status response))
            "should signal a bad request"))))

(deftest read-handler
  (testing "success"
    (let [rate (mocks/mock-rate)
          id (:db/id rate)
          request {:path-params {:id id}}
          response (a.rates/read-handler request)]
      (is (= status/ok (:status response))))))
