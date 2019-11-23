(ns dinsro.actions.rates-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.config :as d.config]
            [dinsro.actions.rates :as a.rates]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [ring.mock.request :as mock]
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

(deftest create-handler
  (testing "success"
    (let [request (gen/generate (s/gen :create-rates-valid/request))
          response (a.rates/create-handler request)
          id (get-in (timbre/spy :info response) [:body :item :db/id])
          created-record (m.rates/read-record id)]
      (is (= status/ok (:status response)))
      (is (= (:name request) (::s.rates/name response)))))
  (testing "invalid params"
    (let [params {}
          request {:params params}
          response (a.rates/create-handler request)]
      (is (= status/bad-request (:status response))
          "should signal a bad request"))))

(deftest read-handler
  (testing "success"
    (let [rate (m.rates/mock-record)
          id (:db/id rate)
          request {:path-params {:id id}}
          response (a.rates/read-handler request)]
      (is (= status/ok (:status response))))


    )
  )
