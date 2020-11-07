(ns dinsro.components.datepicker-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [value-key ::foo
      setter-key ::set-foo
      store (doto (mock-store)
              (st/reg-basic-sub value-key)
              (st/reg-set-event value-key))
      props {:on-select #(st/dispatch store [setter-key %])}]
  (defcard-rg datepicker
    [:<>
     [:pre (pr-str @(st/subscribe store [value-key]))]
     [c.datepicker/datepicker props]])

  (deftest datepicker-text
    (is (vector? (c.datepicker/datepicker props)))))
