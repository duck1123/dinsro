(ns dinsro.actions.authentication-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.users :as m.users]
            [dinsro.specs :as ds]
            [dinsro.spec.actions.authentication :as s.a.authentication]
            [dinsro.spec.users :as s.users]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [ring.util.http-status :as status]))

(def url-root "/api/v1")

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest authenticate-handler-successful
  (let [{:keys [dinsro.spec.users/email
                dinsro.spec.users/password]
         :as user-params} (ds/gen-key ::s.users/params)]
    (m.users/create-record user-params)
    (let [body {:email email :password password}
          path (str url-root "/authenticate")
          request (-> (mock/request :post path) (assoc :params body))
          response (a.authentication/authenticate-handler request)]
      (is (= (:status response) status/ok)))))

(deftest authenticate-handler-failure
  (let [{:keys [dinsro.spec.users/email
                dinsro.spec.users/password]
         :as user-params} (ds/gen-key ::s.users/params)]
    (m.users/create-record user-params)
    (let [body {:email email :password (str password "x")}
          path (str url-root "/authenticate")
          request (-> (mock/request :post path) (assoc :params body))
          response (a.authentication/authenticate-handler request)]
      (is (= (:status response) status/unauthorized)))))

(deftest register-handler-test-success
  (let [request (ds/gen-key ::s.a.authentication/register-request-valid)
        response (a.authentication/register-handler request)]
    (is (= (:status response) status/ok))))

(deftest register-handler-test-invalid-params
  (let [params {}
        request {:params params}
        response (a.authentication/register-handler request)]
    (is (= status/bad-request (:status response)))))
