(ns dinsro.actions.users-test
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.actions.users :as a.users]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.user :as m.users]
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

(deftest create-user-response-test
  (let [registration-data (gen/generate (s/gen ::s.users/params))]
    (let [response (a.users/create-handler {:params registration-data})]
      (is (= (:status response) status/ok)))))

(deftest index-handler-test
  (let [path "/users"]
   (testing "successful"
     (let [request (mock/request :get path)]
       (let [response (a.users/index-handler request)]
         (is (= (:status response) status/ok)))))
   (testing "with record"
     (let [created-user (m.users/mock-user)
           request      (mock/request :get path)
           response     (a.users/index-handler request)]
       (is (= (:status response) status/ok))
       (is (= 1 (count (:body response))))))))

(deftest read-handler
  (testing "when found"
    (let [params                (gen/generate (s/gen ::s.users/params))
          id                    (m.users/create-user! params)
          request               {:path-params {:userId id}}
          response              (a.users/read-handler request)]
      (is (= status/ok (:status response)))
      (are [key] (= (get params key) (get-in response [:body key]))
        :id :email)))
  (testing "when not found"
    (let [id       (gen/generate (s/gen :db/id))
          request  {:path-params {:userId id}}
          response (a.users/read-handler request)]
      (is (= (:status response) status/not-found) "Should return a not-found response"))))
