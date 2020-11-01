(ns dinsro.actions.authentication-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.users :as a.users]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.authentication :as s.a.authentication]
   [dinsro.specs.users :as s.users]
   [dinsro.test-helpers :refer [start-db]]
   [ring.mock.request :as mock]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(def url-root "/api/v1")

(use-fixtures
  :once
  (fn [f]
    (start-db f [s.users/schema])))

(deftest authenticate-handler-successful
  (let [{:keys [dinsro.specs.users/email
                dinsro.specs.users/password]
         :as user-params} (ds/gen-key ::s.users/input-params-valid)]
    (m.users/create-record (a.users/prepare-record user-params))
    (let [body {:email email :password password}
          path (str url-root "/authenticate")
          request (-> (mock/request :post path) (assoc :params body))
          response (a.authentication/authenticate-handler request)]
      (is (= (:status response) status/ok)))))

(deftest authenticate-handler-failure
  (let [{:keys [dinsro.specs.users/email
                dinsro.specs.users/password]
         :as user-params} (ds/gen-key ::s.users/input-params-valid)]
    (m.users/create-record (a.users/prepare-record user-params))
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
