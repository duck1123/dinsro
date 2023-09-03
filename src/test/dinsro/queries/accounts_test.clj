(ns dinsro.queries.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.rate-sources :as o.rate-sources]
   [dinsro.options.users :as o.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

;; [[../../../main/dinsro/queries/accounts.clj]]

(def model-key o.accounts/id)

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create!
  (let [user           (mocks/mock-user)
        user-id        (o.users/id user)
        currency       (mocks/mock-currency)
        currency-id    (o.currencies/id currency)
        source         (mocks/mock-rate-source currency-id)
        source-id      (o.rate-sources/id source)
        params         (ds/gen-key m.accounts/required-params)
        params         (-> params
                           (assoc o.accounts/currency currency-id)
                           (assoc o.accounts/source source-id)
                           (assoc o.accounts/user user-id))
        id             (q.accounts/create! params)
        created-record (q.accounts/read-record id)]
    (assertions
     (get params o.accounts/name) => (get created-record o.accounts/name))))

(deftest read-record-not-found
  (let [id (ds/gen-key model-key)]
    (is (nil? (q.accounts/read-record id)))))

(deftest read-record-found
  (let [record (mocks/mock-account)
        id     (model-key record)]
    (assertions
     "Should return mocked account"
     (q.accounts/read-record id) => record)))

(deftest delete!-success
  (let [account (mocks/mock-account)
        id      (model-key account)]
    (assertions
     "record should exist"
     (q.accounts/read-record id) => account

     "should return nil"
     (q.accounts/delete! id) =fn=> (comp not nil?)

     "record should note exist"
     (q.accounts/read-record id) => nil)))
