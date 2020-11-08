(ns dinsro.ui.forms.add-currency-rate-test
  (:require
   [cljs.pprint :as p]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [dinsro.ui.forms.add-currency-rate :as u.f.add-currency-rate]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.f.add-currency-rate/init-handlers!
                e.f.create-rate/init-handlers!)]
    store))

(let [currency-id (ds/gen-key :db/id)
      store (test-store)
      expected-result @(st/subscribe store [::e.f.add-currency-rate/form-data])]
  (st/dispatch store [::e.f.add-currency-rate/set-shown? true])

  (let [form-data @(st/subscribe store [::e.f.add-currency-rate/form-data])]
    (assert-spec ::e.f.add-currency-rate/form-data expected-result)

    (defcard-rg form-data-card
      [:pre (with-out-str (p/pprint form-data))]))

    (defcard-rg form
      [u.f.add-currency-rate/form store currency-id]))
