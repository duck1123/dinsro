(ns dinsro.actions.authentication-test
  (:require [clojure.test :refer :all]
            [dinsro.actions.authentication :as actions.authentication]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*db*)
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest check-auth
  (testing "successful"
    (db/delete-users!)
    (let [email "test2@example.com"
          password "hunter2"
          user-params {:name "bob"
                       :email email
                       :password password}
          user (model.user/create-user! user-params)
          response (actions.authentication/check-auth email password)]
      (is response true))))
