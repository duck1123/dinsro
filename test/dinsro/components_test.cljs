(ns dinsro.components-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard defcard-rg deftest]]
   [dinsro.components :as c]
   [dinsro.components.admin-index-accounts-test]
   [dinsro.components.admin-index-categories-test]
   [dinsro.components.admin-index-rate-sources-test]
   [dinsro.components.admin-index-users-test]
   [dinsro.components.buttons-test]
   [dinsro.components.currency-rates-test]
   [dinsro.components.datepicker-test]
   [dinsro.components.index-accounts-test]
   [dinsro.components.index-rates-test]
   [dinsro.components.index-transactions-test]
   [dinsro.components.navbar-test]
   [dinsro.components.rate-chart-test]
   [dinsro.components.show-account-test]
   [dinsro.components.show-currency-test]
   ;; [dinsro.components.show-transaction-test]
   [dinsro.components.show-user-test]
   [dinsro.components.status-test]
   [dinsro.components.user-accounts-test]
   [dinsro.components.user-categories-test]
   [dinsro.components.user-transactions-test]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.rate-sources/init-handlers!
                e.users/init-handlers!)]
    store))

(def rate-sources (ds/gen-key (s/coll-of ::s.rate-sources/item :count 3)))

(let [field :foo
      label "Label"
      accounts (ds/gen-key ::e.accounts/items)
      currencies (ds/gen-key (s/coll-of ::s.currencies/item :count 3))
      users (ds/gen-key ::e.users/items)
      handler [::event-name]]

  (comment (defcard currencies currencies))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

    (defcard-rg currency-selector
      [c/currency-selector store label field])

    (deftest currency-selector-test
      (is (vector? (c/currency-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)

    (defcard-rg checkbox-input
      [c/checkbox-input store label field handler])

    (deftest checkbox-input-test
      (is (vector? (c/checkbox-input store label field handler)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

    (defcard-rg account-selector
      [c/account-selector store label field])

    (deftest account-selector-test
      (is (vector? (c/account-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (comment (defcard rate-sources-card rate-sources))

    (defcard-rg rate-source-selector
      [c/rate-source-selector store label field])

    (deftest rate-source-selector-test
      (is (vector? (c/rate-source-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.users/do-fetch-index-success {:users users}])

    (defcard-rg user-selector
      [c/user-selector store label field])

    (deftest user-selector-test
      (is (vector? (c/user-selector store label field))))))
