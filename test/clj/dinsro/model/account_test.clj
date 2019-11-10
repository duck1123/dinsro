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

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* m.accounts/schema)
      (f))))

(deftest create-account!
  (testing "success"
    (let [params (gen/generate (s/gen ::m.accounts/params))
          id (m.accounts/create-account! params)
          created-record (m.accounts/read-account id)]
      (is (= (get params ::name) (get created-record ::name))))))

(deftest index-records
  (testing "success"
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* m.accounts/schema)
      (is (= [] (m.accounts/index-records)))
      (d/transact db/*conn* {:tx-data [{::m.accounts/name "foo"}]})
      (let [expected {::m.accounts/name "foo"}
            actual (m.accounts/index-records)]
        (is (every? #(= "foo" (::m.accounts/name %) (::m.accounts/name expected))
                    actual))))))

(deftest read-account-test
  (testing "not found"
    (let [id (gen/generate (s/gen ::m.accounts/id))]
      (is (= nil (m.accounts/read-account id)))))
  (testing "found"
    (let [account (mock-account)
          id (:db/id account)]
      (is (= account (m.accounts/read-account id))))))
