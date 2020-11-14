(ns dinsro.actions.rates-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.rates :as a.rates]
   [dinsro.mocks :as mocks]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.rates :as s.a.rates]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.rates :as s.rates]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(use-fixtures
  :each
  (fn [f]
    (start-db f [s.currencies/schema s.rates/schema])
    ))

(deftest index-handler
  (let [request {}
        response (a.rates/index-handler request)]
    (is (= (:status response) status/ok))
    (let [body (:body response)
          items (:items body)]
      (is (= [] items)))
    #_(is (= true response))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.rates/create-request-valid)
        response (a.rates/create-handler request)]
    (is (= status/ok (:status response)))
    (let [id (get-in response [:body :item :db/id])]
      (is (not (nil? ident?)))
      (let [created-record (q.rates/read-record id)]
        (is (not (nil? created-record))
            "Created record can be read")
        (is (= (:name request) (::s.rates/name response)))))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.rates/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest read-handler
  (let [rate (mocks/mock-rate)
        id (:db/id rate)
        request {:path-params {:id (str id)}}
        response (a.rates/read-handler request)]
    (is (= status/ok (:status response)))))

(deftest index-by-currency-handler
  (let [request (ds/gen-key ::s.a.rates/index-by-currency-request)
        response (a.rates/index-by-currency-handler request)]
    (is (= status/ok (:status response)))))
