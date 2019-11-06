(ns dinsro.model.currencies-test
  (:require [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.currencies :as m.currencies]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(deftest index-test
  (testing "success"
    (is (= [] (m.currencies/index)))))
