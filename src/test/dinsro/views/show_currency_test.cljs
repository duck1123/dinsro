(ns dinsro.views.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.specs.currencies :as s.currencies]
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

  (defcard-rg page-loaded
    [v.show-currency/page-loaded store currency])

  (deftest page-loaded-test
    (is (vector? (v.show-currency/page-loaded store currency))))

  (defcard-rg page-card
    [v.show-currency/page store match])

  (deftest page-test
    (is (vector? (v.show-currency/page store match)))))
