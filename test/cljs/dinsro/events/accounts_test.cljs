(ns dinsro.events.accounts-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard-rg deftest]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            #_[dinsro.views.login :as v.login]
            [expound.alpha :as expound]
            [taoensso.timbre :as timbre]))

(defcard-rg form
  ;; "**Documentation**"
  (fn [name] [:p (str @name)])
  {:name "foo"})

(deftest sub-item
  (testing "no match"
    (let [items []
          id 1]
      (is (= nil (e.accounts/sub-item items [::e.accounts/item id])))))
  (testing "match"
    (let [id 1
          item {:db/id id}
          items [item]
          response (e.accounts/sub-item items [::e.accounts/item id])]
      (is (= item response))
      (s/assert ::s.accounts/item response)
      #_(defcard-rg
        (expound/expound-str ::s.accounts/item response)))))
