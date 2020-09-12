(ns dinsro.events.forms.create-transaction-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.forms.create-transaction-test
 "Create Transaction Form Events" [])

(let [account-id (ds/gen-key ::s.e.f.create-transaction/account-id)
      date (ds/gen-key ::s.e.f.create-transaction/date)
      description (ds/gen-key ::s.e.f.create-transaction/description)
      value (ds/gen-key ::s.e.f.create-transaction/value)
      db {::s.e.f.create-transaction/account-id account-id
          ::s.e.f.create-transaction/date date
          ::s.e.f.create-transaction/description description
          ::s.e.f.create-transaction/value value}
      event [::e.f.create-transaction/form-data]
      expected-result {:account-id account-id
                       :date date
                       :description description
                       :value value}
      result (e.f.create-transaction/form-data-sub db event)]

  (comment (defcard db db))
  (comment (defcard event event))
  (comment (defcard expected-result expected-result))
  (comment (defcard result result))

  (assert-spec ::e.f.create-transaction/form-data-db db)
  (assert-spec ::e.f.create-transaction/form-data-event event)
  (assert-spec ::e.f.create-transaction/form-data expected-result)
  (assert-spec ::e.f.create-transaction/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
