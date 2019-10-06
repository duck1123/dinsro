(ns dinsro.routes.authentication-test
  (:require [clojure.test :refer :all]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [dinsro.model.user :as model.user]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def url-root "/api/v1")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'handler/app-routes)
    (f)))

(deftest authenticate-test
  (let [email "test@example.com"
        password "hunter2"
        user-params {:email email :name "Bob" :password password}]
   (testing "successful"
      (db/delete-users!)
      (let [user (model.user/create-user! user-params)
            body {:email email :password password}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (mock/json-body body))
            response ((handler/app) request)]
        (is (= status/ok (:status response)))))
   (testing "failure"
     (db/delete-users!)
     (let [user (model.user/create-user! user-params)
           body {:email email :password (str password "x")}
           path (str url-root "/authenticate")
           request (-> (mock/request :post path) (mock/json-body body))
           response ((handler/app) request)]
       (is (= status/unauthorized (:status response)))))))

(deftest register-test
  (let [email "test@example.com"
        password "hunter2"
        path (str url-root "/register")]
    (testing "successful"
      (db/delete-users!)
      (let [params {:email email :name "Bob" :password password}
            request (-> (mock/request :post path) (mock/json-body params))
            response ((handler/app) request)]
        (is (= status/ok (:status response)))))
    (testing "invalid params"
      (let [params {}
            request (-> (mock/request :post path) (mock/json-body params))
            response ((handler/app) request)]
        (is (= status/bad-request (:status response)))))))
