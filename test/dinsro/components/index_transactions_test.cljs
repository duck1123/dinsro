(ns dinsro.components.index-transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary]
   [dinsro.components.index-transactions :as c.index-transactions]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.specs :as ds]
   [dinsro.specs.transactions :as s.transactions]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(let [items (ds/gen-key (s/coll-of ::s.transactions/item))
      transaction-store (fn []
                          (doto (mock-store)
                            e.debug/init-handlers!
                            e.accounts/init-handlers!))]

  (let [item (first items)
        store (transaction-store)]
    (defcard-rg row-line
      [c.index-transactions/row-line store item])

    (deftest row-line-test
      (is (vector? (c.index-transactions/row-line store item)))))

  (let [store (transaction-store)]
    (defcard-rg c.index-transactions/index-transactions
      [c.index-transactions/index-transactions store items])

    (deftest index-transactions-test
      (is (vector? (c.index-transactions/index-transactions store items))))))
