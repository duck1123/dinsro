(ns dinsro.ui.index-transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.boundary]
   [dinsro.ui.index-transactions :as u.index-transactions]))

(let [items (ds/gen-key (s/coll-of ::m.transactions/item))
      transaction-store (fn []
                          (doto (mock-store)
                            e.debug/init-handlers!
                            e.accounts/init-handlers!))]

  (let [item (first items)
        store (transaction-store)]
    (defcard-rg row-line
      [u.index-transactions/row-line store item])

    (deftest row-line-test
      (is (vector? (u.index-transactions/row-line store item)))))

  (let [store (transaction-store)]
    (defcard-rg index-transactions
      [u.index-transactions/index-transactions store items])

    (deftest index-transactions-test
      (is (vector? (u.index-transactions/index-transactions store items))))))
