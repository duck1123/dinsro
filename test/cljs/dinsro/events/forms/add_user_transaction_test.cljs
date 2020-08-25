(ns dinsro.events.forms.add-user-transaction-test
  (:require
   [cljs.test :refer [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1 "Add User Transaction Form Events"]
   [:ul
    [:a {:href "devcards.html#!/dinsro.components.forms.registration_test"}
     "Form Component"]]])

(defcard params
  (ds/gen-key ::s.a.transactions/create-params-valid))

(deftest form-data-sub-test
  (let [date (ds/gen-key ::s.a.transactions/date)
        description (ds/gen-key ::s.a.transactions/description)
        value (ds/gen-key ::s.a.transactions/value)
        cofx [date value description]
        kw ::e.f.add-user-transaction/form-data
        account-id 1
        event [kw account-id]
        expected-result {:account-id account-id
                         :date date
                         :description description
                         :value value}]
    (is (= expected-result (e.f.add-user-transaction/form-data-sub cofx event)))))
