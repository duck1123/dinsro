(ns dinsro.ui.currency-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as log]))

(defn ui-rate-chart
  [_props])

(defsc IndexCurrencyRateLine
  [_this {::m.rates/keys [date id rate]}]
  {:ident         ::m.rates/id
   :initial-state {::m.rates/date ""
                   ::m.rates/id   nil
                   ::m.rates/rate 0}
   :query         [::m.rates/id
                   ::m.rates/date
                   ::m.rates/rate]}
  (dom/tr {}
    (dom/td date)
    (dom/td rate)
    (dom/td (u.buttons/ui-delete-rate-button {::m.rates/id id}))))

(def ui-index-currency-rate-line (comp/factory IndexCurrencyRateLine {:keyfn ::m.rates/id}))

(defsc IndexCurrencyRates
  [_this {::keys [items]}]
  {:initial-state {::items         []}
   :query         [{::items (comp/get-query IndexCurrencyRateLine)}]}
  (if (seq items)
    (dom/table :.ui.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:date]))
          (dom/th (tr [:rate]))
          (dom/th (tr [:actions]))))
      (dom/tbody {}
        (map ui-index-currency-rate-line items)))
    (dom/p "no items")))

(def ui-index-currency-rates (comp/factory IndexCurrencyRates))

(defsc CurrencyRates
  [_this {:keys  [rate-feed]
          ::keys [currency-rates]}]
  {:ident         (fn [] [:component/id ::CurrencyRates])
   :initial-state {:currency/id     1
                   ::currency-rates {}
                   :rate-feed       []}
   :query         [:currency/id
                   {::currency-rates (comp/get-query IndexCurrencyRates)}
                   :rate-feed]}
  (bulma/box
   (dom/h2 {}
     "Rates")
   (dom/hr)
   (ui-rate-chart rate-feed)
   (ui-index-currency-rates currency-rates)))

(def ui-currency-rates (comp/factory CurrencyRates))
