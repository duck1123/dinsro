(ns dinsro.model.users-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.users :as m.users]
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

#_(deftest prepare-record
  (let [params {}
        response (m.users/prepare-record params)]
    (is (= {}  response))))

(deftest create-record-valid
  (testing "successful"
    (let [id-key "user-id"
          params (ds/gen-key ::s.users/params)
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          user (m.users/read-record id)]
      (is (= email (::s.users/email user))))))

(deftest create-record-invalid
  (testing "duplicate creates"
    (let [params (ds/gen-key ::s.users/params)
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          user (m.users/read-record id)]
      (is (thrown? RuntimeException (m.users/create-record params))))))

(deftest read-record
  (testing "success"
    (let [params (ds/gen-key ::s.users/params)
          {:keys [dinsro.spec.users/email]} params
          id (m.users/create-record params)
          response (m.users/read-record id)]
      (is (= email (::s.users/email response))))))
