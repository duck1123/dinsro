(ns dinsro.views-test
  (:require
   [clojure.test :refer [deftest]]
   [dinsro.views.admin-index-accounts-test]
   [dinsro.views.index-accounts-test]
   [dinsro.views.index-transactions-test]
   [dinsro.views.registration-test]
   [fulcro-spec.core :refer [assertions]]))

(deftest views
  (assertions
   "placeholder"
   true => true))
