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
    (f)))

(deftest index-records
  (testing "success"
    (is (= [] (m.accounts/index-records)))
    (let [expected {:account/name "foo"}
          actual (m.accounts/index-records)]
      (is (every? #(= "foo" (:account/name %) (:account/name expected))
                  actual)))))
