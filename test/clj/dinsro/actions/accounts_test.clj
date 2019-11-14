(ns dinsro.actions.accounts-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.actions.account :as a.accounts]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [dinsro.model.user :as m.users]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
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
    (let [request {:params {}}]
      (is [] (a.accounts/index-handler request))))
  (testing "with-records"
    (let [user (m.accounts/mock-account)
          request {}
          response (a.accounts/index-handler request)
          {{:keys [items]} :body} response]
      (is (= [user] items)))))

(deftest create-handler
  (testing "success"
    (let [request (gen/generate (s/gen ::a.accounts/create-handler-request-valid))
          response (a.accounts/create-handler request)]
      (is (= status/ok (:status response)))
      #_(is (= nil response))))
  (testing "invalid params"
    (let [request {:params {}}
          response (a.accounts/create-handler request)]
      (is (= status/bad-request (:status response)))
      #_(is (= nil response)))))

(comment
  (gen/generate (s/gen ::a.accounts/create-handler-request-valid))


  )
