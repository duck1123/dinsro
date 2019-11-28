(ns dinsro.model.currencies-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [datahike.schema :as dhs]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.model.user :as m.users]
            [dinsro.specs :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
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
      (d/transact db/*conn* s.currencies/schema)
      (f))))

(deftest create-record-test
  (testing "success"))

(deftest read-record
  (testing "success"
    (let [item (m.currencies/mock-record)
          id (:db/id item)
          response (m.currencies/read-record id)]
      (is (= item response))))
  (testing "not found"
    (let [id (gen/generate (s/gen ::ds/id))
          response (m.currencies/read-record id)]
      (is (nil? response)))))

(deftest index-records-success
  (testing "success"
    (m.currencies/delete-all)
    (is (= [] (m.currencies/index-records)))))

(deftest index-records-with-records
  (testing "with records"
    (is (not= nil (m.users/mock-record)))
    (let [params (gen/generate (s/gen ::s.currencies/params))
          id (m.currencies/create-record params)]
      (is (not= [params] (m.currencies/index-records))))))

(deftest delete-record
  (testing "success"
    (let [currency (m.currencies/mock-record)
          id (:db/id currency)]
      (is (not (nil? (m.currencies/read-record id))))
      (let [response (m.currencies/delete-record id)]
        (is (nil? response))
        (is (nil? (m.currencies/read-record id)))))))

(comment
  (gen/generate (s/gen ::ds/id))
  )
