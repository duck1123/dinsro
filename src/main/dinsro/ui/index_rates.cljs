(ns dinsro.ui.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexRateLine
  [_this {::m.rates/keys [currency date id rate]}]
  {:ident         ::m.rates/id
   :initial-state {::m.rates/currency {}
                   ::m.rates/date     ""
                   ::m.rates/id       nil
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

(def form-page-toggle-sm ::form-toggle)

(defsc IndexRatesPage
  [this {::keys [show-form-button rates form]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-page-toggle-sm {:actor/navbar IndexRatesPage})
   :ident             (fn [] [:page/id ::page])
   :initial-state     {::form             {}
                       ::rates            {}
                       ::show-form-button {:form-button/id form-page-toggle-sm}}
   :query             [{::form (comp/get-query u.f.create-rate/CreateRateForm)}
                       {::rates (comp/get-query IndexRates)}
                       {::show-form-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]
   :route-segment     ["rates"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-rates IndexRateLine
               {:target [:page/id
                         ::page
                         ::rates
                         ::rates]})
     (dr/route-immediate (comp/get-ident IndexRatesPage {})))}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/page
     (bulma/box
      (dom/h1
       (tr [:index-rates "Index Rates"])
       (u.buttons/ui-show-form-button show-form-button))
      (when shown? (u.f.create-rate/ui-create-rate-form form))
      (dom/hr)
      (ui-index-rates rates)))))

(def ui-page (comp/factory IndexRatesPage))
