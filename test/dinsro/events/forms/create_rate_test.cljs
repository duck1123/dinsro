(ns dinsro.events.forms.create-rate-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [defcard deftest]]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(defcard form-data (ds/gen-key ::e.f.create-rate/form-data))
(comment (defcard form-data-db (ds/gen-key ::e.f.create-rate/form-data-db)))
(comment (defcard form-data-event (pr-str (ds/gen-key ::e.f.create-rate/form-data-event))))

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

  (defcard db db)
  (assert-spec ::e.f.create-rate/form-data-db db)

  (defcard event event)
  (assert-spec ::e.f.create-rate/form-data-event event)

  (defcard expected-result expected-result)
  (assert-spec ::e.f.create-rate/form-data expected-result)

  (defcard result result)
  (assert-spec ::e.f.create-rate/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
