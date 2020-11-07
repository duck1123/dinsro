(ns dinsro.components.forms.create-rate-test
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard defcard-rg]]
   [dinsro.components.forms.create-rate :as c.f.create-rate]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [currencies (ds/gen-key (s/coll-of ::e.currencies/item :count 3))
      store (doto (mock-store)
              e.currencies/init-handlers!
              e.debug/init-handlers!
              e.f.create-account/init-handlers!
              e.f.create-rate/init-handlers!
              e.f.add-user-account/init-handlers!)]

  (comment (defcard currencies currencies))

  (st/dispatch store [::e.debug/set-shown? true])
  (st/dispatch store [::e.f.create-rate/set-shown? true])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

  (defcard-rg form-data
    [:pre (pr-str @(st/subscribe store [::e.f.create-rate/form-data]))])

  (defcard-rg form
    [c.f.create-rate/form store]))
