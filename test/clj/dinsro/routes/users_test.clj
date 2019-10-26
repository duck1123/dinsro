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
          expected-body {:users []}
          parsed-body (json/read-str (slurp body) :key-fn keyword)]
      (is (= 200 status))
      (is (= expected-body parsed-body)))))

(deftest read-users
  (testing "successful"
    (db/delete-users!)
    (let [name "test"
          email "duck@kronkltd.net"
          params {:name name :email email :password-hash "foo"}
          {:keys [id]} (db/create-user! params)
          path (str "/api/v1/users/" id)
          request (mock/request :get path)
          {:keys [body status]} ((handler/app) request)
          received-user (json/read-str (slurp body) :key-fn keyword)
          {received-email :email received-id :id} received-user]
      (are [a b] (= a b)
        received-email email
        received-id    id)))
  (testing "not found"
    (db/delete-users!)
    (let [id 1
          path (str "/api/v1/users/" id)
          request (mock/request :get path)
          {:keys [status]} ((handler/app) request)]
      (is (= status 404) "Should return a not-found response"))))
