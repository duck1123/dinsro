(ns dinsro.queries.users-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.specs.users :as s.users]
   [mount.core :as mount]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest create-record-valid
  (let [params (ds/gen-key ::s.users/params)
        {:keys [dinsro.specs.users/email]} params
        id (q.users/create-record params)
        user (q.users/read-record id)]
    (is (= email (::s.users/email user)))))

(deftest create-record-invalid
  (let [params (ds/gen-key ::s.users/params)
        id (q.users/create-record params)]
    (q.users/read-record id)
    ;; TODO: Throw a better error
    (is (thrown? RuntimeException (q.users/create-record params)))))

(deftest read-record
  (let [params (ds/gen-key ::s.users/params)
        {:keys [dinsro.specs.users/email]} params
        id (q.users/create-record params)
        response (q.users/read-record id)]
    (is (= email (::s.users/email response)))))