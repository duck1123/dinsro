(ns dinsro.components.datepicker-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.datepicker :as c.datepicker]))

(cards/header
 'dinsro.components.datepicker-test
 "Datepicker Components" [])

(let [props {}]
  (defcard-rg datepicker
    (fn []
      [error-boundary
       [c.datepicker/datepicker props]]))

  (deftest datepicker-text
    (is (vector? (c.datepicker/datepicker props)))))
