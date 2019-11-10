(ns dinsro.actions.currencies-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.currencies :as m.currencies]
            [mount.core :as mount]
            [taoensso.timbre :as timbre]))

(def example-request {:name "foo"})

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      ;; (d/transact db/*conn* m.users/schema)
      (d/transact db/*conn* m.currencies/schema)
      (f))))

(defn gen-spec
  [spec]
  (gen/generate (s/gen spec)))

(deftest index-handler-test
  (testing "success - no records"
    (let [request {}
          response (a.currencies/index-handler request)
          items (get-in response [:body :items])]
      (is (= [] items))))
  (testing "success - with records"
    (let [record (m.currencies/mock-record)
          request {}
          response (a.currencies/index-handler request)
          items (get-in response [:body :items])]
      (is (= [record] items)))
    )
  )

(deftest create-handler
  (testing "success"
    (let [request (gen-spec :create-handler-valid/request)
          response (a.currencies/create-handler request)
          id (get-in response [:body :item])
          created-record (m.currencies/read-record id)]
      (is (= (:name request) (::m.currencies/name response))))))

(comment
  (gen-spec ::a.currencies/create-handler-request)

  (gen-spec :create-handler-valid/request)


  )
