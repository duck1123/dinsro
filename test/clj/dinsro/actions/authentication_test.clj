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
            [dinsro.model.user :as m.users]
            [dinsro.specs :as ds]
            [luminus-migrations.core :as migrations]
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
      #_(d/transact db/*conn* m.accounts/schema)
      (d/transact db/*conn* m.users/schema)
      (f))))

(deftest check-auth
  (testing "successful"
    (let [user-params (gen/generate (s/gen ::m.users/registration-params))
          email (::m.users/email user-params)
          password (::m.users/password user-params)
          user (m.users/create-user! user-params)
          response (a.authentication/check-auth email password)]
      (is (= true response)))))

(deftest authenticate-test
  (let [{:keys [dinsro.model.user/email
                dinsro.model.user/password]
         :as user-params} (gen/generate (s/gen ::m.users/registration-params))]
    (testing "successful"
      (let [user (m.users/create-user! user-params)
            body {:email email :password password}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (assoc :params body))
            response (a.authentication/authenticate-handler request)]
        (is (= (:status response) status/ok))))
    (testing "failure"
      (let [user (m.users/create-user! user-params)
            body {:email email :password (str password "x")}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (assoc :params body))
            response (a.authentication/authenticate-handler request)]
        (is (= (:status response) status/unauthorized))))))

(deftest register-handler-test
  (let [path (str url-root "/register")]
    (testing "successful"
      (let [params (gen/generate (s/gen ::m.users/registration-params))
            request (-> (mock/request :post path)
                        (assoc :params params))
            request (gen/generate (s/gen ::a.authentication/register-request))
            response (a.authentication/register-handler request)]
        (is (= (:status response) status/ok))))
    (testing "invalid params"
      (let [params {}
            request (-> (mock/request :post path) (assoc :params params))
            response (a.authentication/register-handler request)]
        (is (= (:status response) status/bad-request))))))
