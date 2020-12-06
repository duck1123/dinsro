(ns dinsro.events.forms.add-account-transaction-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [assert-spec deftest]]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-transaction :as s.e.f.create-transaction]))

(let [account-id (ds/gen-key ::s.e.f.create-transaction/account-id)
      date (ds/gen-key ::s.e.f.create-transaction/date)
      description (ds/gen-key ::s.e.f.create-transaction/description)
      value (ds/gen-key ::s.e.f.create-transaction/value)
      expected-result {:account-id account-id
                       :date date
                       :description description
                       :value (.parseFloat js/Number value)}
      db {::s.e.f.create-transaction/date date
          ::s.e.f.create-transaction/description description
          ::s.e.f.create-transaction/value value}
      event [::e.f.add-account-transaction/form-data account-id]
      result (e.f.add-account-transaction/form-data-sub db event)]

  (assert-spec ::e.f.add-account-transaction/form-data-db db)
  (assert-spec ::e.f.add-account-transaction/form-data-event event)
  (assert-spec ::e.f.add-account-transaction/form-data expected-result)
  (assert-spec ::e.f.add-account-transaction/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
