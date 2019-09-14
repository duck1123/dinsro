(ns dinsro.routes.users-test
  (:require [clojure.data.json :as json]
            [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [dinsro.db.core :as db]
            [dinsro.handler :refer :all]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'dinsro.config/env
                 #'dinsro.handler/app)
    (f)))

(deftest index-users
  (testing "successful response with no users"
    (db/delete-users!)
    (let [{:keys [body status]} (app (request :get "/api/v1/users"))
          expected-body []
          read-body (json/read-str body :key-fn keyword)]
      (is (= 200 status))
      (is (= expected-body read-body)))))

(deftest read-users
  (testing "successful"
    (db/delete-users!)
    (let [name "test"
          email "duck@kronkltd.net"
          params {:name name
                  :email email
                  :password_hash "foo"}
          {:keys [id]} (db/create-user! params)
          path (str "/api/v1/users/" id)
          {:keys [body status]} (app (request :get path))
          {received-email :email
           received-id :id} body]
      (is (= received-email email))
      (is (= received-id id)))))
