(ns dinsro.events.accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :as cards]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [expound.alpha :as expound]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.accounts-test
 "Account Events" [])

(let [items (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      item (first items)
      item-map (into {} (map (fn [item] [(:db/id item) item]) items))
      db {::e.accounts/item-map item-map}]

  (comment (defcard db db))
  (comment (defcard item-map item-map))
  (comment (defcard items items))

  (let [id (inc (last (sort (map :db/id items))))
        response (e.accounts/item-sub db [::e.accounts/item id])]

    (comment (defcard id (pr-str id)))

    (deftest sub-item-no-match
      (is (= nil response))
      (s/assert ::s.accounts/item response)
      (expound/expound-str ::s.accounts/item response)))

  (let [id (:db/id item)
        response (e.accounts/item-sub db [::e.accounts/item id])]

    (comment (defcard id (pr-str id)))

    (deftest sub-item-match
      (is (= item response))
      (s/assert ::s.accounts/item response)
      (expound/expound-str ::s.accounts/item response))))
