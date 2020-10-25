(ns dinsro.components.buttons-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.buttons-test
 "Button Components" [])

(let [account (ds/gen-key ::e.accounts/item)
      store (mock-store)]
  (comment (defcard account account))

  (defcard-rg delete-account
    (fn []
      [error-boundary
       [c.buttons/delete-account store account]]))

  (deftest delete-account-test
    (is (vector? (c.buttons/delete-account store account)))))

(let [store (mock-store)
      category nil]
  (defcard-rg delete-category
    (fn []
      [error-boundary
       [c.buttons/delete-category store category]]))

  (deftest delete-category-test
    (is (vector? (c.buttons/delete-category store category)))))

(let [store (mock-store)
      currency nil]
  (defcard-rg delete-currency
    (fn []
      [error-boundary
       [c.buttons/delete-currency store currency]]))

  (deftest delete-currency-test
    (is (vector? (c.buttons/delete-currency store currency)))))

;; delete-rate

(let [store (mock-store)
      rate (ds/gen-key ::e.rates/item)]
  (comment (defcard rate rate))

  (defcard-rg delete-rate
    (fn []
      [error-boundary
       [c.buttons/delete-rate store rate]]))

  (deftest delete-rate-test
    (is (vector? (c.buttons/delete-rate store rate)))))

;; fetch-accounts

(let [store (doto (mock-store)
              e.accounts/init-handlers!)]
  (defcard-rg fetch-accounts
    (fn []
      [error-boundary
       [c.buttons/fetch-accounts store]]))

  (deftest fetch-accounts-test
    (is (vector? (c.buttons/fetch-accounts store)))))

;; fetch-categories

(let [store (doto (mock-store)
              e.categories/init-handlers!)]
  (defcard-rg fetch-categories
    (fn []
      [error-boundary
       [c.buttons/fetch-categories store]]))

  (deftest fetch-categories-test
    (is (vector? (c.buttons/fetch-categories store)))))

;; fetch-currencies

(let [store (doto (mock-store)
              e.currencies/init-handlers!)]
  (defcard-rg fetch-currencies
    (fn []
      [error-boundary
       [c.buttons/fetch-currencies store]]))

  (deftest fetch-currencies-test
    (is (vector? (c.buttons/fetch-currencies store)))))

;; fetch-rate-sources

(let [store (doto (mock-store)
              e.rate-sources/init-handlers!)]
  (defcard-rg fetch-rate-sources
    (fn []
      [error-boundary
       [c.buttons/fetch-rate-sources store]]))

  (deftest fetch-rate-sources-test
    (is (vector? (c.buttons/fetch-rate-sources store)))))

;; fetch-rates

(let [store (doto (mock-store)
              e.rates/init-handlers!)]
  (defcard-rg fetch-rates
    (fn []
      [error-boundary
       [c.buttons/fetch-rates store]]))

  (deftest fetch-rates-test
    (is (vector? (c.buttons/fetch-rates store)))))

;; fetch-transactions

(let [store (doto (mock-store)
              e.transactions/init-handlers!)]
  (defcard-rg fetch-transactions
    (fn []
      [error-boundary
       [c.buttons/fetch-transactions store]]))

  (deftest fetch-transactions-test
    (is (vector? (c.buttons/fetch-transactions store)))))

;; fetch-users

(let [store (doto (mock-store)
              e.users/init-handlers!)]
  (defcard-rg fetch-users
    (fn []
      [error-boundary
       [c.buttons/fetch-users store]]))

  (deftest fetch-users
    (is (vector? (c.buttons/fetch-users store)))))