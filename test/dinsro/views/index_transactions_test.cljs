(ns dinsro.views.index-transactions-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.index-transactions :as v.index-transactions]
   [taoensso.timbre :as timbre]))

(let [items (ds/gen-key (s/coll-of ::s.transactions/item :count 5))
      index-transaction-store
      (fn []
        (let [store (doto (mock-store)
                      e.accounts/init-handlers!
                      e.currencies/init-handlers!
                      e.debug/init-handlers!
                      e.f.create-transaction/init-handlers!
                      e.transactions/init-handlers!)]
          (st/dispatch store [::e.debug/set-shown? true])
          (st/dispatch store [::e.transactions/do-fetch-index-success {:items items}])
          store))
      match nil]

  (let [store (index-transaction-store)]
    (defcard-rg v.index-transactions/section-inner
      (fn []
        [error-boundary
         [v.index-transactions/section-inner store items]]))

    (deftest section-inner-test
      (is (vector? (v.index-transactions/section-inner store items)))))

  (let [store (index-transaction-store)]
    (defcard-rg page-card
      (fn []
        [error-boundary
         (v.index-transactions/page store match)]))

    (deftest page-test
      (is (vector? (v.index-transactions/page store match))))))
