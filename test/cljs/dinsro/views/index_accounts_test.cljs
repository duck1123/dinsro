(ns dinsro.views.index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.index-accounts :as v.index-accounts]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(cards/header "Index Accounts View" [])

(let [items (ds/gen-key (s/coll-of ::s.accounts/item :count 2))
      template nil
      data nil
      result nil
      path "/accounts"
      path-params {}
      store (doto (mock-store)
              e.debug/init-handlers!
              e.accounts/init-handlers!
              e.authentication/init-handlers!
              e.currencies/init-handlers!
              e.users/init-handlers!
              e.authentication/init-handlers!)
      match (rc/->Match template data result path-params path)]

  (defcard items items)

  (defcard-rg page-card
    (fn []
      (st/dispatch store [::e.authentication/set-auth-id 1])
      (st/dispatch store [::e.accounts/do-fetch-index-success {:items items}])
      [error-boundary
       [v.index-accounts/page store match]]))

  (defcard-rg page-card-unauthenticated
    (fn []
      (st/dispatch store [::e.authentication/set-auth-id nil])
      (st/dispatch store [::e.accounts/do-fetch-index-success {:items items}])
      [error-boundary
       [v.index-accounts/page store match]]))

  (deftest page-test
    (is (vector? (v.index-accounts/page store match)))))
