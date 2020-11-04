(ns dinsro.components.forms.add-user-transaction-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard defcard-rg deftest]]
   [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(let [accounts (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      store (doto (mock-store)
              e.accounts/init-handlers!
              e.debug/init-handlers!
              e.f.add-user-transaction/init-handlers!
              e.f.create-transaction/init-handlers!
              e.transactions/init-handlers!)]

  (comment (defcard accounts accounts))

  (st/dispatch store [::e.f.add-user-transaction/set-shown? true])
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

  (defcard-rg form
    [c.f.add-user-transaction/form-shown store])

  (deftest form-test
    (is (vector? (c.f.add-user-transaction/form store)))))
