(ns dinsro.actions.accounts-test
  (:require [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.actions.account :as a.accounts]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [dinsro.model.user :as m.users]
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
      (d/transact db/*conn* m.users/schema)
      (d/transact db/*conn* m.accounts/schema)
      (f))))

(deftest index-handler-test
  (testing "success"
    (let [request {}]
      (is [] (a.accounts/index-handler request))))
  (testing "with-records"
    (let [user (m.accounts/mock-account)
          request {}
          response (a.accounts/index-handler request)
          {{:keys [items]} :body} response]
      (is (= [user] items)))))
