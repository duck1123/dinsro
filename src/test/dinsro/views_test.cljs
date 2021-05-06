(ns dinsro.views-test
  (:require
   [dinsro.views.admin-index-accounts-test]
   [dinsro.views.index-accounts-test]
   [dinsro.views.index-transactions-test]
   [dinsro.views.registration-test]
   [fulcro-spec.core :refer [assertions specification]]))

(specification "views"
  (assertions
   "placeholder"
   true => true))
