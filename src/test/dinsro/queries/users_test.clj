(ns dinsro.queries.users-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata
  [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-valid
  (let [params   (ds/gen-key ::m.users/params)
        username (::m.users/name params)
        id       (q.users/create-record params)
        user     (q.users/read-record id)]
    (assertions
     (::m.users/name user) => username)))

(deftest create-record-invalid
  (let [params (ds/gen-key ::m.users/params)
        _id    (q.users/create-record params)]
    (assertions
     (q.users/create-record params) =throws=> RuntimeException)))

(deftest read-record-success
  (let [params                  (ds/gen-key ::m.users/params)
        {::m.users/keys [name]} params
        id                      (q.users/create-record params)
        response                (q.users/read-record id)]
    (assertions
     (::m.users/name response) => name)))

(deftest read-record-not-found
  (let [id (ds/gen-key :xt/id)]
    (assertions
     (q.users/read-record id) => nil)))

(deftest read-records-empty
  (assertions
   (q.users/read-records []) => []))

(deftest read-records-existing
  (let [n 2]
    (dotimes [_ n] (mocks/mock-user))
    (let [records (q.users/index-records)
          ids     (q.users/index-ids)]
      (assertions
       (q.users/read-records ids) => records))))
