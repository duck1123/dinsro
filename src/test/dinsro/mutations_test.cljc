(ns dinsro.mutations-test
  (:require
   [clojure.test :refer [deftest]]
   [dinsro.mutations :as m]
   [fulcro-spec.core :refer [assertions]]))

(deftest error-response
  (let [message "foo"]
    (assertions
     (:message (::m/errors (m/error-response message))) => message)))
