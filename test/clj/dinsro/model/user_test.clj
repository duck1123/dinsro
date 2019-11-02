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
    (f)))

(deftest create-user!
  (testing "successful"
    (let [params {:name "bob"
                  :email "test@example.com"
                  :password "hunter2"}
          {:keys [email]} params
          response (model.user/create-user! params)]
      (is (= (:email response) email)))))

(deftest read-user
  (testing "success"
    (let [user-id 1
          response (model.user/read-user user-id)]
      (is (= user-id (:id response))))))
