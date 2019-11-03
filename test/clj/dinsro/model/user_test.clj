(ns dinsro.model.user-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [dinsro.model.user :as m.user]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

#_(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*db*)
    (f)))

(deftest create-user!
  (testing "successful"
    (let [params (gen/generate (s/gen ::m.user/registration-params))
          {:keys [dinsro.model.user/email]} params
          id (m.user/create-user! params)
          user (m.user/read-user id)]
      (is (= (::m.user/email user) email)))))

(deftest read-user
  (testing "success"
    (let [params (gen/generate (s/gen ::m.user/registration-params))
          {:keys [dinsro.model.user/email]} params
          id (m.user/create-user! params)
          response (m.user/read-user id)]
      (is (= id (:db/id response)))
      (is (= email (::m.user/email response))))))
