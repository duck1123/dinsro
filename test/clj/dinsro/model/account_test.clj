(ns dinsro.model.account-test
  (:require [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(def uri "datahike:mem://example")

(deftest index-records
  (testing "success"
    (is (= [{:account/name "foo"}] (m.accounts/index-records)))))
