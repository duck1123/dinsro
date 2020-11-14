(ns dinsro.actions.accounts-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.accounts :as a.accounts]
   [dinsro.mocks :as mocks]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs :as ds]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.specs.actions.accounts :as s.a.accounts]
   [dinsro.specs.users :as s.users]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(use-fixtures
  :each
  (fn [f]
    (start-db f [s.users/schema
                 s.accounts/schema])))

(deftest index-handler-test-empty
  (let [request {:session {:identity 1}
                 :params {}}
        response (a.accounts/index-handler request)
        items (get-in response [:body :items])]
    (is (= status/ok (:status response)))
    (is (= [] items))))

(deftest index-handler-test-with-records
  (let [record (mocks/mock-account)
        user-id (get-in record [::s.accounts/user :db/id])
        request {:session {:identity user-id}}
        response (a.accounts/index-handler request)
        {{:keys [items]} :body} response]
    (is (= [record] items))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.accounts/create-request-valid)
        response (a.accounts/create-handler request)]
    (is (= status/ok (:status response)))))

(deftest create-handler-no-initial-value
  (let [request (assoc (ds/gen-key ::s.a.accounts/create-request-valid)
                       ::s.accounts/initial-value 0)
        response (a.accounts/create-handler request)]
    (is (= status/ok (:status response)))))

(deftest create-handler-no-currency
  (let [request (ds/gen-key ::s.a.accounts/create-request-valid-no-currency)
        response (a.accounts/create-handler request)]
    (is (= status/ok (:status response)))))

(deftest create-handler-invalid
  (let [request {:params {}}
        response (a.accounts/create-handler request)]
    (is (= status/bad-request (:status response)))))

(deftest read-handler
  (let [account (mocks/mock-account)
        id (:db/id account)
        request {:path-params {:id (str id)}}
        response (a.accounts/read-handler request)]
    (is (= status/ok (:status response)))
    (let [body (:body response)]
      (= account body))))

(deftest delete-handler
  (let [account (mocks/mock-account)
        id (:db/id account)
        request {:path-params {:id (str id)}}
        response (a.accounts/delete-handler request)]
    (is (= status/ok (:status response))
        "successful status")

    (is (nil? (q.accounts/read-record id))
        "account is deleted")))
