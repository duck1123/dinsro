(ns dinsro.views.index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [dinsro.views.index-accounts :as v.index-accounts]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.views.index-accounts-test
 "Index Accounts View"
 [#{:accounts :views} #{:accounts}])

(defn test-app
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.authentication/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.f.add-user-account/init-handlers!
                e.f.create-account/init-handlers!
                e.users/init-handlers!)]
    store))

(let [currencies (ds/gen-key (s/coll-of ::s.currencies/item :count 3))
      items (ds/gen-key (s/coll-of ::s.accounts/item :count 2))
      template nil
      data nil
      result nil
      path "/accounts"
      path-params {}
      match (rc/->Match template data result path-params path)]

  (comment (defcard items items))
  (comment (defcard currencies currencies))

  (let [store (test-app)
        form-data (st/subscribe store [::e.f.add-user-account/form-data])]
    (st/dispatch store [::e.authentication/set-auth-id 1])
    (st/dispatch store [::e.f.add-user-account/set-shown? true])
    (st/dispatch store [::e.accounts/do-fetch-index-success {:items items}])
    (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

    (defcard-rg page-card
      (fn []
        [error-boundary
         [v.index-accounts/page store match]]))

    (defcard-rg form-data (fn [] (pr-str @form-data)))
    (let [form-data-sub @form-data]
      (assert-spec ::e.f.add-user-account/form-data form-data-sub))

    (deftest page-test
      (is (vector? (v.index-accounts/page store match)))))

  (let [store (test-app)]
    (st/dispatch store [::e.authentication/set-auth-id nil])
    (st/dispatch store [::e.accounts/do-fetch-index-success {:items items}])

    (defcard-rg page-unauthenticated
      (fn []
        [error-boundary
         [v.index-accounts/page store match]]))

    (deftest page-unauthenticated-test
      (is (vector? (v.index-accounts/page store match))))))