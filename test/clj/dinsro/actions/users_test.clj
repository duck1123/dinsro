(ns dinsro.actions.users-test
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [dinsro.actions.users :as a.users]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            [dinsro.specs :as ds]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*db*)
    (f)))

(deftest index-handler-test
  (let [path "/users"]
   (testing "successful"
     (let [request (mock/request :get path)]
       (let [response (a.users/index-handler request)]
         (is (= (:status response) status/ok)))))
   (testing "with record"
     (let [params (gen/generate (s/gen ::ds/user))
           created-user (db/create-user! t-conn params)
           id (get created-user :id)
           request (mock/request :get path)
           response (a.users/index-handler request)]
       (is (= (:status response) status/ok))
       (is (= 1 (count (:body response))))))))

(deftest read-handler
  (testing "when found"
    (let [params                (gen/generate (s/gen ::ds/register-request))
          {:keys [id] :as user} (model.user/create-user! params)
          request               {:path-params {:userId id}}
          response              (a.users/read-handler request)]
      (is (= status/ok (:status response)))
      (are [key] (= (get user key) (get-in response [:body key]))
        :id :email)))
  (testing "when not found"
    (db/delete-users!)
    (let [id       (gen/generate (s/gen ::ds/user-id))
          request  {:path-params {:userId id}}
          response (a.users/read-handler request)]
      (is (= (:status response) status/not-found) "Should return a not-found response"))))
