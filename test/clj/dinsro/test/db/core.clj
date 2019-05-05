(ns dinsro.test.db.core
  (:require [dinsro.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [dinsro.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'dinsro.config/env
      #'dinsro.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [name "Sam"
          email "sam.smith@example.com"
          password-hash "pass"
          params {:name name
                  :email email
                  :password_hash password-hash}
          created-user (db/create-user! t-conn params)
          id (get created-user :id)
          response (db/read-user t-conn {:id id})]
      (doseq [[key expected] [[:id            1]
                              [:name          name]
                              [:email         email]
                              [:password-hash password-hash]]]
        (is (get response key) expected)))))