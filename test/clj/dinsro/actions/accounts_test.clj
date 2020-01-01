(ns dinsro.actions.accounts-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.actions.accounts :as a.accounts]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.accounts :as m.accounts]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.actions.accounts :as s.a.accounts]
            [dinsro.spec.users :as s.users]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (d/transact db/*conn* s.accounts/schema)
      (f))))

(deftest index-handler-test-empty
  (let [request {:params {}}
        response (a.accounts/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [] items))))

(deftest index-handler-test-with-records
  (let [user (mocks/mock-account)
        request {}
        response (a.accounts/index-handler request)
        {{:keys [items]} :body} response]
    (is (= [user] items))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.accounts/create-request-valid)
        response (a.accounts/create-handler request)]
    (is (= status/ok (:status response)))
    #_(is (= nil response))))

(deftest create-handler-invalid
  (let [request {:params {}}
        response (a.accounts/create-handler request)]
    (is (= status/bad-request (:status response)))
    #_(is (= nil response))))

(deftest delete-handler
  (let [account (mocks/mock-account)
        id (:db/id account)
        request {:path-params {:id (str id)}}
        response (a.accounts/delete-handler request)]
    (is (= status/ok (:status response)) "successful status")
    (is (nil? (m.accounts/read-record id)) "account is deleted")))
