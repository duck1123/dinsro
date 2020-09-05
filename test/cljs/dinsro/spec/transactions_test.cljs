(ns dinsro.spec.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [expound.alpha :as expound]
   [tick.alpha.api :as tick]))

(defcard-rg title
  [:div
   [:h1.title "Transaction Spec"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec_test"}
      "Specs"]]]

   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.show_transaction_test"}
      "Show Transaction Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.transactions_test"}
      "Transaction Specs"]]]])

(defcard generated-item
  (ds/gen-key ::s.transactions/item))

(let [item {:db/id 1
            ::s.transactions/account {:db/id 1}
            ::s.transactions/date (tick/instant)
            ::s.transactions/description "Foo"
            ::s.transactions/value -3}]
  (defcard item item)

  (defcard-rg item-test-validation
    [:pre (expound/expound-str ::s.transactions/item item)])

  (deftest item-test
    (is (s/valid? ::s.transactions/item item))))
