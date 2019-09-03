(ns dinsro.routes.authentication-test
  (:require [clojure.test :refer :all]
            [dinsro.handler :as handler]
            [mount.core :as mount]
            [ring.mock.request :refer :all]))

(def url-root "/api/v1")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'dinsro.config/env
                 #'dinsro.handler/app)
    (f)))

(deftest authenticate-test
  (testing "successful"
    (let [response (handler/app (request :post (str url-root "/authenticate")))]
      (is (= 200 (:status response))))))

(deftest register-test)
