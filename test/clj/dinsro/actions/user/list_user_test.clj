(ns dinsro.actions.user.list-user-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*db*)
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest list-user-response-test
  (testing "successful"
    (let [path "/users"
          request (mock/request :get path)]
      (let [response (list-user-response request)]
        (is (= 200 (:status response))))))
  (testing "with record"
    (jdbc/with-db-transaction [t-conn db/*db*]
      (jdbc/db-set-rollback-only! t-conn)
      (let [name "Sam"
            email "sam.smith@example.com"
            password-hash "pass"
            params {:name name
                    :email email
                    :password_hash password-hash}
            created-user (db/create-user! t-conn params)
            id (get created-user :id)
            path "/users"
            request (mock/request :get path)
            response (list-user-response request)]
        (is (= 200 (:status response)))
        (is (= 1 (count (:body response))))))))
