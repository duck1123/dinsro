(ns dinsro.model.account-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example")

(defn test-db
  [f]
  (d/delete-database uri)
  (when-not (d/database-exists? (datahike.config/uri->config uri))
    (d/create-database uri))
  (with-redefs [db/*conn* #_(datahike.core/create-conn #_m.accounts/schema) (d/connect uri)]
    (d/transact db/*conn* m.accounts/schema)
    (f)))

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (test-db f)
    #_(f)
    ))

(comment
  @(d/transact! (datahike.core/create-conn) m.accounts/schema)

  )

(deftest create-account!
  (testing "success"
    (let [params (gen/generate (s/gen ::m.accounts/params))
          id (m.accounts/create-account! params)
          created-record (m.accounts/read-account id)]
      (is (= (get params ::name) (get created-record ::name))))))

(deftest index-records
  (testing "success - no record"
    (is (= [] (m.accounts/index-records)))))

(deftest read-account-test
  (testing "not found"
    (let [id (gen/generate (s/gen ::m.accounts/id))]
      (is (= nil (m.accounts/read-account id)))))
  (testing "found"
    (let [account (m.accounts/mock-account)
          id (:db/id account)]
      (is (= account (m.accounts/read-account id))))))
