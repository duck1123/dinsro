(ns dinsro.ui.forms.add-user-transaction-test
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.forms.add-user-transaction :as u.f.add-user-transaction]))

(let [accounts (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      store (doto (mock-store)
              e.accounts/init-handlers!
              e.debug/init-handlers!
              e.f.add-user-transaction/init-handlers!
              e.f.create-transaction/init-handlers!
              e.transactions/init-handlers!)]

  (st/dispatch store [::e.f.add-user-transaction/set-shown? true])
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

  (defcard-rg form
    [u.f.add-user-transaction/form-shown store]))
