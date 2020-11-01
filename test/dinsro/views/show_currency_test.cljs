(ns dinsro.views.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.show-currency :as v.show-currency]
   [taoensso.timbre :as timbre]))

(let [currency (ds/gen-key ::s.currencies/item)
      store (doto (mock-store)
              e.accounts/init-handlers!
              e.currencies/init-handlers!
              e.debug/init-handlers!
              e.rates/init-handlers!
              e.rate-sources/init-handlers!)
      match {:path-params {:id "1"}}]

  (defcard currency currency)

  (comment (defcard init-page-cofx (ds/gen-key ::s.v.show-currency/init-page-cofx)))
  (comment (defcard init-page-event (ds/gen-key ::s.v.show-currency/init-page-event)))
  (comment (defcard init-page-response (ds/gen-key ::s.v.show-currency/init-page-response)))
  (comment (defcard view-map (ds/gen-key ::s.v.show-currency/view-map)))

  (defcard-rg v.show-currency/page-loaded
    (fn []
      [error-boundary
       [v.show-currency/page-loaded store currency]]))

  (deftest page-loaded-test
    (is (vector? (v.show-currency/page-loaded store currency))))

  (defcard-rg page-card
    (fn []
      [error-boundary
       [v.show-currency/page store match]]))

  (deftest page-test
    (is (vector? (v.show-currency/page store match)))))
