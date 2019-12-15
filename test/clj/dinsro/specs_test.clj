(ns dinsro.specs-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

(deftest id-string-test
  (is (s/valid? ::ds/id-string "1")
      "Valid number")

  (is (not (s/valid? ::ds/id-string 1))
      "integer is invalid")

  (is (not (s/valid? ::ds/id-string "a"))
      "non-numeric string is invalid"))

(comment
  (gen/generate (s/gen ::ds/id-string))
  )
