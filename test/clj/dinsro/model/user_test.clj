(ns dinsro.model.user-test
  (:require [clojure.test :refer :all]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'config/env
     #'db/*db*)
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest create-user!
  (testing "successful"
    (db/delete-users!)
    (let [params {:name "bob"
                  :email "test@example.com"
                  :password "hunter2"}
          response (model.user/create-user! params)]
      (is response true))))
