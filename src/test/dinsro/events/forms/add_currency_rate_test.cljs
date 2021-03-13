(ns dinsro.events.forms.add-currency-rate-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [assert-spec deftest]]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-rate :as s.e.f.create-rate]))

(let [currency-id (ds/gen-key ::s.e.f.create-rate/currency-id)
      date (ds/gen-key ::s.e.f.create-rate/date)
      rate (ds/gen-key ::s.e.f.create-rate/rate)
      expected-result {:date date
                       :rate (.parseFloat js/Number rate)
                       :currency-id (.parseInt js/Number currency-id)}
      db {::s.e.f.create-rate/date date
          ::s.e.f.create-rate/rate rate}

      event [::e.f.add-currency-rate/form-data currency-id]
      result (e.f.add-currency-rate/form-data-sub db event)]

  (assert-spec ::e.f.add-currency-rate/form-data-db db)
  (assert-spec ::e.f.add-currency-rate/form-data-event event)
  (assert-spec ::e.f.add-currency-rate/form-data expected-result)
  (assert-spec ::e.f.add-currency-rate/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
