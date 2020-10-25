(ns dinsro.events.forms.add-account-transaction-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha]
   [dinsro.cards :as cards :include-macros true]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.test-utils :refer-macros [assert-spec]]))

(cards/header
 'dinsro.events.forms.add-account-transaction-test
 "Add Account Transaction Form Events" [#{:accounts}])

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

      ;; (ds/gen-key ::e.f.add-account-transaction/form-data-db)
      event [::e.f.add-account-transaction/form-data account-id]
      result (e.f.add-account-transaction/form-data-sub db event)]

  (defcard db db)
  (assert-spec ::e.f.add-account-transaction/form-data-db db)

  (defcard event event)
  (assert-spec ::e.f.add-account-transaction/form-data-event event)

  (defcard expected-result expected-result)
  (assert-spec ::e.f.add-account-transaction/form-data expected-result)

  (defcard result result)
  (assert-spec ::e.f.add-account-transaction/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
