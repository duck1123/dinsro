(ns dinsro.components-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components :as c]
   [dinsro.components.admin-index-accounts-test]
   [dinsro.components.admin-index-categories-test]
   [dinsro.components.admin-index-rate-sources-test]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.currency-rates-test]
   [dinsro.components.index-transactions-test]
   [dinsro.components.rate-chart-test]
   [dinsro.components.show-account-test]
   [dinsro.components.show-currency-test]
   [dinsro.components.status-test]
   [taoensso.timbre :as timbre]))

(cards/header "Components" [])

(defcard-rg checkbox-input
  (fn []
    [error-boundary
     [c/checkbox-input "foo" :foo]]))

(deftest account-selector
  (let [label "foo"
        field :foo]
    (is (vector? (c/account-selector label field)))))
