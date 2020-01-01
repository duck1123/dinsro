(ns dinsro.events.accounts-test
  (:require [cljs.test :refer-macros [
                                      #_deftest
                                      is testing]]
            [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard-rg
                                          deftest
                                          ]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [expound.alpha :as expound]
            [taoensso.timbre :as timbre]))

(defcard-rg form
  ;; "**Documentation**"
  (fn [name] [:p (str @name)])
  {:name "foo"})

(deftest sub-item-no-match
  (testing "no match"
    (let [items []
          id 1]
      (is (= nil (e.accounts/sub-item items [::e.accounts/item id])))))
  )

(deftest sub-item-match
  (testing "match"
    (let [id 1
          item {:db/id id}
          items [item]
          response (e.accounts/sub-item items [::e.accounts/item id])]
      (is (= item response))
      (s/assert ::s.accounts/item response)
      (defcard-rg
        (expound/expound-str ::s.accounts/item response)))))
