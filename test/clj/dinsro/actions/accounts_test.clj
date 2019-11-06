(ns dinsro.actions.accounts-test
  (:require [clojure.test :refer :all]
            [dinsro.actions.account :as a.accounts]
            [dinsro.model.account :as m.accounts]
            [orchestra.core :refer [defn-spec]]))

(defn-spec mock-account ::m.accounts/account
  []
  {})

(deftest index-handler-test
  (testing "success"
    (let [request {}]
      (is [] (a.accounts/index-handler request))))
  (testing "with-records"
    (let [user (mock-account)
          request {}
          response (a.accounts/index-handler request)
          {{:keys [items]} :body} response]
      (is (= [user] items #_response)))))
