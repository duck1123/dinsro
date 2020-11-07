(ns dinsro.events.forms.create-rate-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(let [currency-id (ds/gen-key ::s.e.f.create-rate/currency-id)
      date (ds/gen-key ::s.e.f.create-rate/date)
      rate (ds/gen-key ::s.e.f.create-rate/rate)
      db {::s.e.f.create-rate/currency-id currency-id
          ::s.e.f.create-rate/date date
          ::s.e.f.create-rate/rate rate}
      event [::e.f.create-rate/form-data]
      expected-result {:currency-id (.parseInt js/Number currency-id)
                       :date (str (tick/instant date))
                       :rate (.parseFloat js/Number rate)}
      result (e.f.create-rate/form-data-sub db event)]

  (assert-spec ::e.f.create-rate/form-data-db db)
  (assert-spec ::e.f.create-rate/form-data-event event)
  (assert-spec ::e.f.create-rate/form-data expected-result)
  (assert-spec ::e.f.create-rate/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
