(ns dinsro.actions.user.list-user-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
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
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest list-user-response-test
  (let [path "/users"]
   (testing "successful"
     (let [request (mock/request :get path)]
       (let [response (list-user-response request)]
         (is (= (:status response) status/ok)))))
   (testing "with record"
     (let [params (gen/generate (s/gen ::ds/user))
           created-user (db/create-user! params)
           id (get created-user :id)
           request (mock/request :get path)
           response (list-user-response request)]
       (is (= (:status response) status/ok))
       (is (= 1 (count (:body response))))))))
