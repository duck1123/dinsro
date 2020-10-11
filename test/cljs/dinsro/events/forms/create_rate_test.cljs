(ns dinsro.events.forms.create-rate-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha]
   [devcards.core :refer-macros [defcard
                                 ;; defcard-rg
                                 deftest]]
   [dinsro.cards :as cards]
   ;; [dinsro.components.boundary :refer [error-boundary]]
   ;; [dinsro.components.forms.create-account :as c.f.create-account]
   ;; [dinsro.components.forms.create-rate :as c.f.create-rate]
   ;; [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec :as ds]
   ;; [dinsro.spec.actions.rates :as s.a.rates]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   ;; [dinsro.store.mock :refer [mock-store]]
   [dinsro.test-utils :refer-macros [assert-spec]]
   [tick.alpha.api :as tick]))

(cards/header
 'dinsro.events.forms.create-rate-test
 "Create Rate Form Events"
 [#{:forms :rates} #{:rates}])

(defcard form-data (ds/gen-key ::e.f.create-rate/form-data))
(comment (defcard form-data-db (ds/gen-key ::e.f.create-rate/form-data-db)))
(comment (defcard form-data-event (pr-str (ds/gen-key ::e.f.create-rate/form-data-event))))

(let [currency-id (ds/gen-key ::s.e.f.create-rate/currency-id)
      date (ds/gen-key ::s.e.f.create-rate/date)
      rate (ds/gen-key ::s.e.f.create-rate/rate)
      db {::s.e.f.create-rate/currency-id currency-id
          ::s.e.f.create-rate/date date
          ::s.e.f.create-rate/rate rate}
      event [::e.f.create-rate/form-data]
      expected-result {:currency-id (.parseInt js/Number currency-id)
                       :date (tick/instant date)
                       :rate (.parseFloat js/Number rate)}
      result (e.f.create-rate/form-data-sub db event)]

  (defcard db db)
  (assert-spec ::e.f.create-rate/form-data-db db)

  (defcard event event)
  (assert-spec ::e.f.create-rate/form-data-event event)

  (defcard expected-result expected-result)
  (assert-spec ::e.f.create-rate/form-data expected-result)

  (defcard result result)
  (assert-spec ::e.f.create-rate/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
