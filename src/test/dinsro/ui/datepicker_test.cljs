(ns dinsro.ui.datepicker-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.boundary]
   [dinsro.ui.datepicker :as u.datepicker]
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
     [u.datepicker/datepicker props]])

  (deftest datepicker-text
    (is (vector? (u.datepicker/datepicker props)))))
