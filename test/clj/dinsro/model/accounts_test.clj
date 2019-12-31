(ns dinsro.model.accounts-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.accounts :as m.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example")

(defn test-db
  [f]
  (d/delete-database uri)
  (when-not (d/database-exists? (uri->config uri))
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
  (let [params (ds/gen-key ::s.accounts/params)
        id (m.accounts/create-record params)
        created-record (m.accounts/read-record id)]
    (is (= (get params ::s.accounts/name) (get created-record ::s.accounts/name)))))

(deftest index-records
  (is (= [] (m.accounts/index-records))))

(deftest read-record-not-found
  (let [id (gen/generate (s/gen ::ds/id))]
    (is (= nil (m.accounts/read-record id)))))

(deftest read-record-found
  (let [record (mocks/mock-account)
        id (:db/id record)]
    (is (= record (m.accounts/read-record id)))))

(deftest delete-record
  (let [account (mocks/mock-account)
        id (:db/id account)]
    (is (not (nil? (m.accounts/read-record id))))
    (let [response (m.accounts/delete-record id)]
      (is (not (nil? response)))
      (is (nil? (m.accounts/read-record id))))))
