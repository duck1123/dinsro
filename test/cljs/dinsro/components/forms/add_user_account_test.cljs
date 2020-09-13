(ns dinsro.components.forms.add-user-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.add-user-account :as c.f.add-user-account]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.spec :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header 'dinsro.components.forms.add-user-account-test
              "Add User Account Form Components" [])

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

  (comment (defcard currencies currencies))

  (defcard-rg form-data
    (fn []
      (timbre/info "form data")
      [:pre (pr-str @(st/subscribe store [::e.f.add-user-account/form-data]))]))

  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.add-user-account/form store id]]))

  (deftest form-test
    (is (vector? (c.f.add-user-account/form store id)))))
