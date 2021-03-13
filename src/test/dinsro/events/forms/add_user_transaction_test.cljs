(ns dinsro.events.forms.add-user-transaction-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [assert-spec deftest]]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.transactions :as s.a.transactions]
   [dinsro.specs.events.forms.create-transaction :as s.e.f.create-transaction]
   [taoensso.timbre :as timbre]))

(let [date (ds/gen-key ::s.a.transactions/date)
      description (ds/gen-key ::s.a.transactions/description)
      value (ds/gen-key ::s.a.transactions/value)
      db {::s.e.f.create-transaction/date date
          ::s.e.f.create-transaction/description description
          ::s.e.f.create-transaction/value value}
      kw ::e.f.add-user-transaction/form-data
      account-id 1
      event [kw account-id]
      expected-result {:account-id account-id
                       :date date
                       :description description
                       :value (.parseFloat js/Number value)}
      result (e.f.add-user-transaction/form-data-sub db event)]

  (assert-spec ::e.f.add-user-transaction/form-data-db db)
  (assert-spec ::e.f.add-user-transaction/form-data-event event)
  (assert-spec ::e.f.add-user-transaction/form-data expected-result)
  (assert-spec ::e.f.add-user-transaction/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
