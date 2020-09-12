(ns dinsro.events.admin-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.spec :as ds]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [dinsro.translations :refer [tr]]))

(cards/header
 'dinsro.events.admin-index-accounts-test
 "Admin Accounts Events"
 [#{:accounts} #{:accounts :events}])

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

    (assert-spec ::e.admin-accounts/item-sub-response response)

    (deftest item-sub-no-match
      (is (= nil response))))

  (let [id (:db/id item)
        response (e.admin-accounts/item-sub db [::e.accounts/item id])]
    (comment (defcard id (pr-str id)))

    (assert-spec ::e.admin-accounts/item-sub-response response)

    (deftest item-sub-match
      (is (= item response)))))
