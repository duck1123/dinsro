(ns dinsro.actions.authentication-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.config :as d.config]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [dinsro.model.users :as m.users]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def url-root "/api/v1")

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (d.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest check-auth
  (testing "successful"
    (let [user-params (gen/generate (s/gen ::s.users/params))
          email (::s.users/email user-params)
          password (::s.users/password user-params)
          user (m.users/create-record user-params)
          response (a.authentication/check-auth email password)]
      (is (= true response)))))

(deftest authenticate-handler
  (let [{:keys [dinsro.spec.users/email
                dinsro.spec.users/password]
         :as user-params} (gen/generate (s/gen ::s.users/params))]
    (testing "successful"
      (m.users/delete-all)
      (m.users/create-record user-params)
      (let [body {:email email :password password}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (assoc :params body))
            response (a.authentication/authenticate-handler request)]
        (is (= (:status response) status/ok))))
    (testing "failure"
      (m.users/delete-all)
      (m.users/create-record user-params)
      (let [body {:email email :password (str password "x")}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (assoc :params body))
            response (a.authentication/authenticate-handler request)]
        (is (= (:status response) status/unauthorized))))))

(deftest register-handler-test
  (let [path (str url-root "/register")]
    (testing "successful"
      (m.users/delete-all)
      (let [request (gen/generate (s/gen ::a.authentication/register-request-valid))
            response (a.authentication/register-handler request)]
        (is (= (:status response) status/ok))))
    (testing "invalid params"
      (m.users/delete-all)
      (let [params {}
            request (-> (mock/request :post path) (assoc :params params))
            response (a.authentication/register-handler request)]
        (is (= (:status response) status/bad-request))))))
