(ns dinsro.specs-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer [deftest]]
   [clojure.test.check.generators]
   [dinsro.specs :as ds]
   [fulcro-spec.core :refer [assertions]]
   [taoensso.timbre :as log]))

(deftest id-string
  (assertions
   (s/valid? ::ds/id-string "1") => true
   (s/valid? ::ds/id-string 1) => false
   (s/valid? ::ds/id-string "a") => false))

(deftest date-string
  (assertions
   (s/valid? ::ds/date-string "2019-12-22T14:02:00.000Z") => true))
