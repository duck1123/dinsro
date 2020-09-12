(ns dinsro.events.forms.create-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
   [dinsro.test-utils :refer-macros [assert-spec]]))

(cards/header
 'dinsro.events.forms.create-account-test
 "Create Account Form Events"
 [#{:accounts}])

(let [currency-id (ds/gen-key ::s.e.f.create-account/currency-id)
      initial-value (ds/gen-key ::s.e.f.create-account/initial-value)
      name (ds/gen-key ::s.e.f.create-account/name)
      user-id (ds/gen-key ::s.e.f.create-account/user-id)
      expected-result {:currency-id (int currency-id)
                       :initial-value (.parseFloat js/Number initial-value)
                       :name name
                       :user-id (int user-id)}
      db {::s.e.f.create-account/currency-id currency-id
          ::s.e.f.create-account/initial-value initial-value
          ::s.e.f.create-account/name name
          ::s.e.f.create-account/user-id user-id}
      event [::e.f.create-account/form-data]
      result (e.f.create-account/form-data-sub db event)]

  (comment (defcard db db))
  (comment (defcard result result))

  (assert-spec ::e.f.create-account/form-data-db db)
  (assert-spec ::e.f.create-account/form-data-event event)
  (assert-spec ::e.f.create-account/form-data-response expected-result)
  (assert-spec ::e.f.create-account/form-data-response result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
