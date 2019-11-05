(ns dinsro.model.account-test
  (:require [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (d/transact db/*conn* m.accounts/schema)
    (f)))

(deftest index-records
  (testing "success"
    (is (= [] (m.accounts/index-records)))
    (let [expected {::m.accounts/name "foo"}
          actual (m.accounts/index-records)]
      (is (every? #(= "foo" (::m.accounts/name %) (::m.accounts/name expected))
                  actual)))))
