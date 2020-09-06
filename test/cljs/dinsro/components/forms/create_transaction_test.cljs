(ns dinsro.components.forms.create-transaction-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-transaction :as c.f.create-transaction]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header "Create Transaction Form Components" [])

(let [form-data (ds/gen-key ::e.f.create-transaction/form-data)
      new-data {
                ::s.e.f.create-transaction/account-id 1
                ::s.e.f.create-transaction/date (:date form-data)
                ::s.e.f.create-transaction/description (:description form-data)
                ::s.e.f.create-transaction/value (:value form-data)
                ::e.debug/shown? true}

      store (doto (mock-store)
              e.accounts/init-handlers!
              e.debug/init-handlers!
              e.f.create-transaction/init-handlers!)]

  (defcard new-data new-data)

  (defcard-rg create-transaction-card
    (fn []
      [error-boundary
       [c.f.create-transaction/form store]]))

  (deftest create-transaction-test
    (is (vector? (c.f.create-transaction/form store)))))
