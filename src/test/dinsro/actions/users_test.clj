(ns dinsro.actions.users-test
  (:require
   [clojure.test :refer [are deftest is use-fixtures]]
   [dinsro.actions.users :as a.users]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :refer [start-db]]
   [ring.mock.request :as mock]
   [ring.util.http-status :as status]))

(use-fixtures
  :each
  (fn [f]
    (start-db f [m.users/schema])))

(deftest create-record-response-test
  (let [params (ds/gen-key ::m.users/input-params-valid)
        response (a.users/create-handler {:params params})]
    (is (= (:status response) status/ok))))

(deftest index-handler-empty
  (let [path "/users"
        request (mock/request :get path)
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))))

(deftest index-handler-with-records
  (mocks/mock-user)
  (let [request {}
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))
    (is (= 1 (count (:body response))))))

(deftest read-handler-success
  (let [params (ds/gen-key ::m.users/params)
        id (q.users/create-record params)
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= status/ok (:status response))
        "Should return an ok status")
    (are [key] (= (get params key) (get-in response [:body key]))
      :id :email)))

(deftest read-handler-not-found
  (let [id (ds/gen-key :db/id)
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= (:status response) status/not-found)
        "Should return a not-found response")))
