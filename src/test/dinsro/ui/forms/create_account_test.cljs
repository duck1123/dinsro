(ns dinsro.ui.forms.create-account-test
  (:require
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.forms.create-account :as u.f.create-account]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.f.add-user-account/init-handlers!
                e.f.create-account/init-handlers!
                e.users/init-handlers!)]
    store))

(let [store (test-store)]

  (st/dispatch store [::e.f.create-account/set-shown? true])

  (defcard-rg form
    [u.f.create-account/form store]))
