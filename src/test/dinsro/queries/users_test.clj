(ns dinsro.queries.users-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.components.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/config #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* m.users/schema)
      (f))))

(deftest create-record-valid
  (let [{::m.users/keys [email] :as params} (ds/gen-key ::m.users/params)
        id (q.users/create-record params)
        user (q.users/read-record id)]
    (is (= email (::m.users/email user)))))

(deftest create-record-invalid
  (let [params (ds/gen-key ::m.users/params)
        id (q.users/create-record params)]
    (q.users/read-record id)
    ;; TODO: Throw a better error
    (is (thrown? RuntimeException (q.users/create-record params)))))

(deftest read-record
  (let [{::m.users/keys [email] :as params} (ds/gen-key ::m.users/params)
        id (q.users/create-record params)
        response (q.users/read-record id)]
    (is (= email (::m.users/email response)))))

(deftest read-record-missing
  (let [id (ds/gen-key :db/id)
        response (q.users/read-record id)]
    (is (nil? response))))

(deftest read-records-empty
  (let [ids []
        response (q.users/read-records ids)]
    (is (= [] response))))

(deftest read-records-existing
  (let [n 2]
    (dotimes [_ n] (mocks/mock-user))
    (let [records (q.users/index-records)
          ids (map :db/id records)
          response (q.users/read-records ids)]
      (is (= records response)))))
