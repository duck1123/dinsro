(ns dinsro.views.index-transactions-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.views.index-transactions :as v.index-transactions]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Index Transactions View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box]])

(let [items (ds/gen-key (s/coll-of ::s.transactions/item :count 5))]

  (defcard-rg v.index-transactions/section-inner
    [v.index-transactions/section-inner items])

  (deftest page
    (let [store nil
          match nil]
      (is (vector? (v.index-transactions/page store match)))))

  (defcard-rg page-card
    (let [store nil
          match nil]
      [v.index-transactions/page store match])))
