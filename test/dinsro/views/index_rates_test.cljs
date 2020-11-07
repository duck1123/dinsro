(ns dinsro.views.index-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.index-rates :as v.index-rates]
   [taoensso.timbre :as timbre]))

(let [currency (ds/gen-key ::e.currencies/item)
      rates (map
             (fn [rate] (assoc-in rate [::s.rates/currency :db/id] (:db/id currency)))
             (ds/gen-key (s/coll-of ::e.rates/item :count 3)))
      store (doto (mock-store)
              e.debug/init-handlers!
              e.currencies/init-handlers!
              e.rates/init-handlers!
              e.f.create-rate/init-handlers!)
      match nil]

  (st/dispatch store [::e.rates/do-fetch-index-success {:items rates}])

  (defcard-rg page
    [v.index-rates/page store match])

  (deftest page-test
    (is (vector? (v.index-rates/page store match)))))
