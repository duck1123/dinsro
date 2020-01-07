(ns dinsro.events.accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [expound.alpha :as expound]
   [taoensso.timbre :as timbre]))

(defcard-rg form
  ;; "**Documentation**"
  (fn [name] [:p (str @name)])
  {:name "foo"})

(let [item-map (ds/gen-key ::e.accounts/item-map)]
  (defcard item-map item-map)

  (deftest sub-item-no-match
    (let [id 1
          item-map {}]
      (is (= nil (e.accounts/item-sub item-map [::e.accounts/item id])))))

  (deftest sub-item-match
    (let [id 1
          item {:db/id id}
          item-map {id item}
          event [::e.accounts/item id]
          response (e.accounts/item-sub item-map event)]
      (is (= item response))
      (s/assert ::s.accounts/item response)
      (expound/expound-str ::s.accounts/item response))))
