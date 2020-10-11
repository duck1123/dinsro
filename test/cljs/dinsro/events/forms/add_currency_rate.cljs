(ns dinsro.events.forms.add-currency-rate-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [dinsro.cards :as cards]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.test-utils :refer-macros [assert-spec]]))

(cards/header
 'dinsro.events.forms.add-currency-rate-test
 "Add Currency Rate Form Events"
 [#{:events :forms} #{:rates} #{:rates :forms}])

(let [
      currency-id (ds/gen-key ::s.e.f.create-rate/currency-id)
      date (ds/gen-key ::s.e.f.create-rate/date)
      rate (ds/gen-key ::s.e.f.create-rate/rate)
      expected-result {
                       :date date
                       :rate (.parseFloat js/Number rate)
                       :currency-id (.parseInt js/Number currency-id)
                       }
      db {::s.e.f.create-rate/date date
          ::s.e.f.create-rate/rate rate
          }

      ;; (ds/gen-key ::e.f.add-currency-rate/form-data-db)
      event [::e.f.add-currency-rate/form-data currency-id]
      result (e.f.add-currency-rate/form-data-sub db event)]

  (defcard db db)
  (assert-spec ::e.f.add-currency-rate/form-data-db db)

  (defcard event event)
  (assert-spec ::e.f.add-currency-rate/form-data-event event)

  (defcard expected-result expected-result)
  (assert-spec ::e.f.add-currency-rate/form-data expected-result)

  (defcard result result)
  (assert-spec ::e.f.add-currency-rate/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
