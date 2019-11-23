(ns dinsro.model.account-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example")

(defn test-db
  [f]
  (d/delete-database uri)
  (when-not (d/database-exists? (datahike.config/uri->config uri))
    (d/create-database uri))
  (with-redefs [db/*conn* (d/connect uri)]
    (d/transact db/*conn* s.accounts/schema)
    (f)))

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (test-db f)
    #_(f)))

(deftest create-record
  (testing "success"
    (let [params (gen/generate (s/gen ::s.accounts/params))
          id (m.accounts/create-record params)
          created-record (m.accounts/read-record id)]
      (is (= (get params ::s.accounts/name) (get created-record ::s.accounts/name))))))

(deftest index-records
  (testing "success - no record"
    (is (= [] (m.accounts/index-records)))))

(deftest read-record
  (testing "not found"
    (let [id (gen/generate (s/gen ::s.accounts/id))]
      (is (= nil (m.accounts/read-record id)))))
  (testing "found"
    (let [record (m.accounts/mock-record)
          id (:db/id record)]
      (is (= record (m.accounts/read-record id))))))

(deftest delete-record
  (testing "success"
    (let [account (m.accounts/mock-record)
          id (:db/id account)]
      (is (not (nil? (m.accounts/read-record id))))
      (let [response (m.accounts/delete-record id)]
        (is (not (nil? response)))
        (is (nil? (m.accounts/read-record id)))))))

(comment
  @(d/transact! (datahike.core/create-conn) s.accounts/schema)
  (m.accounts/mock-record)
  (m.accounts/index-records)
  (m.accounts/read-record 40)
  (m.accounts/delete-record 40)
  )
