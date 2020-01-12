(ns dinsro.actions.rates-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.actions.rates :as a.rates]
            [dinsro.config :as config]
            [dinsro.db :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec :as ds]
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
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.rates/schema)
      (f))))

(deftest index-handler
  (let [request {}
        response (a.rates/index-handler request)]
    (is (= (:status response) status/ok))
    (let [body (:body response)
          items (:items body)]
      (is (= [] items)))
    #_(is (= true response))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.rates/create-request-valid)
        response (a.rates/create-handler request)]
    (is (= status/ok (:status response)))
    (let [id (get-in response [:body :item :db/id])]
      (is (not (nil? ident?)))
      (let [created-record (m.rates/read-record id)]
        (is (not (nil? created-record))
            "Created record can be read")
        (is (= (:name request) (::s.rates/name response)))))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.rates/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest read-handler
  (let [rate (mocks/mock-rate)
        id (:db/id rate)
        request {:path-params {:id (str id)}}
        response (a.rates/read-handler request)]
    (is (= status/ok (:status response)))))
