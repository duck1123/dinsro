(ns dinsro.model.user-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.user :as m.users]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest create-record
  (testing "successful"
    (let [id-key "user-id"
          params (gen/generate (s/gen ::s.users/params))
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          user (m.users/read-record id)]
      (is (= email (::s.users/email user)))))
  (testing "duplicate creates"
    (let [params (gen/generate (s/gen ::s.users/params))
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          user (m.users/read-record id)]
      (is (thrown? RuntimeException (m.users/create-record params))))))

(deftest read-record
  (testing "success"
    (let [params (gen/generate (s/gen ::s.users/params))
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          response (m.users/read-record id)]
      (is (= email (::s.users/email response))))))
