(ns dinsro.ui.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexRateLine
  [_this {::m.rates/keys [currency date id rate]}]
  {:ident         ::m.rates/id
   :initial-state {::m.rates/currency {}
                   ::m.rates/date     ""
                   ::m.rates/id       0
                   ::m.rates/rate     0}
   :query         [::m.rates/id
                   {::m.rates/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.rates/date
                   ::m.rates/rate]}
  (dom/tr {}
    (dom/td (u.links/ui-currency-link currency))
    (dom/td date)
    (dom/td rate)
    (dom/td (u.buttons/ui-delete-rate-button {::m.rates/id id}))))

(def ui-index-rate-source-line (comp/factory IndexRateLine {:keyfn ::m.rates/id}))

(defsc IndexRates
  [_this {::keys [rates]}]
  {:initial-state {::rates []}
   :query         [{::rates (comp/get-query IndexRateLine)}]}
  (if (seq rates)
    (dom/table :.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:currency]))
          (dom/th (tr [:date]))
          (dom/th (tr [:rate]))
          (dom/th (tr [:actions]))))
      (dom/tbody {}
        (map ui-index-rate-source-line rates)))
    (dom/p "no items")))

(def ui-index-rates (comp/factory IndexRates))
