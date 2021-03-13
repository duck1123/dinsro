(ns dinsro.ui.forms.admin-create-account-test
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.users/init-handlers!
                e.debug/init-handlers!
                e.f.create-account/init-handlers!
                e.f.add-user-account/init-handlers!)]
    store))

(let [currencies (ds/gen-key (s/coll-of ::e.currencies/item :count 3))
      users (ds/gen-key (s/coll-of ::e.users/item :count 3))
      accounts (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      name "Foo"
      initial-value 42
      store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])
  (st/dispatch store [::e.users/do-fetch-index-success {:items users}])
  (st/dispatch store [::s.e.f.create-account/set-name name])
  (st/dispatch store [::s.e.f.create-account/set-initial-value initial-value])
  (st/dispatch store [::e.f.create-account/set-shown? true])

  (st/dispatch store [::e.debug/set-shown? true])

  (defcard-rg form-inner
    [u.f.admin-create-account/form store])

  (st/dispatch store [::e.debug/set-shown? true])

  (defcard-rg form-inner
    [u.f.admin-create-account/form store]))
