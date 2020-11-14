(ns dinsro.ui.forms.add-user-account-test
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [taoensso.timbre :as timbre]))

(let [currencies (ds/gen-key (s/coll-of ::e.currencies/item :count 3))
      shown? true
      id 1
      store (doto (mock-store)
              e.currencies/init-handlers!
              e.debug/init-handlers!
              e.f.create-account/init-handlers!
              e.f.add-user-account/init-handlers!)]

  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])
  (st/dispatch store [::e.f.add-user-account/set-shown? shown?])

  (defcard-rg form-data
    (timbre/info "form data")
    [:pre (pr-str @(st/subscribe store [::e.f.add-user-account/form-data]))])

  (defcard-rg form
    [u.f.add-user-account/form store id]))
