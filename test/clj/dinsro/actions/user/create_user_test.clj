(ns dinsro.actions.user.create-user-test
  (:require [clojure.test :refer :all]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'config/env
     #'db/*db*)
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest create-user-response-test
  (db/delete-users!)
  (let [registration-data {:name "bob"
                           :email "bob@example.com"
                           :password "hunter22"}]
    (is (create-user-response registration-data))))
