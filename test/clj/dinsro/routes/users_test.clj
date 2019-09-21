(ns dinsro.routes.users-test
  (:require [clojure.data.json :as json]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'handler/app-routes)
    (f)))

(deftest index-users
  (testing "successful response with no users"
    (db/delete-users!)
    (let [path "/api/v1/users"
          request (mock/request :get path)
          {:keys [body status]} ((handler/app) request)
          expected-body []
          read-body (json/read-str body :key-fn keyword)]
      (is (= 200 status))
      (is (= expected-body read-body)))))

(deftest read-users
  (testing "successful"
    (db/delete-users!)
    (let [name "test"
          email "duck@kronkltd.net"
          params {:name name :email email :password_hash "foo"}
          {:keys [id]} (db/create-user! params)
          path (str "/api/v1/users/" id)
          request (mock/request :get path)
          {:keys [body status]} ((handler/app) request)
          {received-email :email
           received-id :id} body]
      (is (= received-email email))
      (is (= received-id id)))))
