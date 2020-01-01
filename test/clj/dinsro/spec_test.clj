(ns dinsro.spec-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is]]
            [clojure.test.check.generators]
            [dinsro.spec :as ds]
            [taoensso.timbre :as timbre]))

;; TODO: move to cards

(deftest id-string-test
  (is (s/valid? ::ds/id-string "1")
      "Valid number")

  (is (not (s/valid? ::ds/id-string 1))
      "integer is invalid")

  (is (not (s/valid? ::ds/id-string "a"))
      "non-numeric string is invalid"))

(deftest date-string-test
  (is (s/valid? ::ds/date-string "2019-12-22T14:02:00.000Z")
      "Valid date"))
