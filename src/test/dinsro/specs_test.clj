(ns dinsro.specs-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators]
   [dinsro.specs :as ds]
   [fulcro-spec.core :refer [assertions specification]]
   [taoensso.timbre :as log]))

;; TODO: move to cards

(specification "id-string"
  (assertions
   (s/valid? ::ds/id-string "1") => true
   (s/valid? ::ds/id-string 1) => false
   (s/valid? ::ds/id-string "a") => false))

(specification "date string"
  (assertions
   (s/valid? ::ds/date-string "2019-12-22T14:02:00.000Z") => true))
