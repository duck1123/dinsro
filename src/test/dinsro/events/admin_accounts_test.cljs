(ns dinsro.events.admin-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.specs :as ds]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [dinsro.translations :refer [tr]]
   [expound.alpha]))

(let [items (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      item (first items)
      item-map (into {} (map (fn [item] [(:db/id item) item]) items))
      db {::e.accounts/item-map item-map}]

  (let [id (inc (last (sort (map :db/id items))))
        response (e.admin-accounts/item-sub db [::e.accounts/item id])]
    (assert-spec ::e.admin-accounts/item-sub-response response)

    (deftest item-sub-no-match
      (is (= nil response))))

  (let [id (:db/id item)
        response (e.admin-accounts/item-sub db [::e.accounts/item id])]
    (assert-spec ::e.admin-accounts/item-sub-response response)

    (deftest item-sub-match
      (is (= item response)))))
