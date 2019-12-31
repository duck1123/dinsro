(ns dinsro.actions.users-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [are deftest is use-fixtures]]
            [datahike.api :as d]
            [dinsro.actions.users :as a.users]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.users :as m.users]
            [dinsro.spec.users :as s.users]
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
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest create-record-response-test
  (let [registration-data (gen/generate (s/gen ::s.users/params))
        response (a.users/create-handler {:params registration-data})]
    (is (= (:status response) status/ok))))

(deftest index-handler-empty
  (let [path "/users"
        request (mock/request :get path)
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))))

(deftest index-handler-with-records
  (let [path "/users"
        _user (mocks/mock-user)
        request (mock/request :get path)
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))
    (is (= 1 (count (:body response))))))

(deftest read-handler-success
  (let [params (gen/generate (s/gen ::s.users/params))
        id (m.users/create-record params)
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= status/ok (:status response))
        "Should return an ok status")
    (are [key] (= (get params key) (get-in response [:body key]))
      :id :email)))

(deftest read-handler-not-found
  (let [id (gen/generate (s/gen ::ds/id))
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= (:status response) status/not-found)
        "Should return a not-found response")))
