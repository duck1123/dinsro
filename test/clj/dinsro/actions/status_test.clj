(ns dinsro.actions.status-test
  (:require
   [clojure.test :refer [deftest is]]
   [dinsro.actions.status :as a.status]
   [taoensso.timbre :as timbre]))

(deftest status-handler-no-identity
  (let [request {}
        response (a.status/status-handler request)]
    (is (= nil (get-in response [:body :identity])))))
