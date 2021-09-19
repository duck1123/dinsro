(ns dinsro.ui.currency-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-currency-rate :as u.f.add-currency-rate]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defn ui-show-form-button
  [_props])

(defn ui-add-currency-rate-form
  [_props])

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
  {:initial-state {::form          {}
                   ::items         []
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query         [{::items (comp/get-query IndexCurrencyRateLine)}
                   {::form (comp/get-query u.f.add-currency-rate/AddCurrencyRateForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   [::uism/asm-id form-toggle-sm]]}
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
  [this {:keys  [rate-feed]
         ::keys [currency-rates form toggle-button]}]
  {:componentDidMount
   #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CurrencyRates})
   :ident         (fn [] [:component/id ::CurrencyRates])
   :initial-state {:currency/id     1
                   ::currency-rates {}
                   :rate-feed       []
                   ::form           {}
                   ::toggle-button  {:form-button/id form-toggle-sm}}
   :query         [:currency/id
                   {::currency-rates (comp/get-query IndexCurrencyRates)}
                   {::form (comp/get-query u.f.add-currency-rate/AddCurrencyRateForm)}
                   :rate-feed
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 {}
       "Rates"
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-currency-rate/ui-form form))
     (dom/hr)
     (ui-rate-chart rate-feed)
     (ui-index-currency-rates currency-rates))))

(def ui-currency-rates (comp/factory CurrencyRates))
