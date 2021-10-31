(ns dinsro.ui.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

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

(defsc IndexRatesPage
  [_this {::keys [rates]}]
  {:ident             (fn [] [:page/id ::page])
   :initial-state     {::rates            {}}
   :query             [{::rates (comp/get-query IndexRates)}]
   :route-segment     ["rates"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-rates IndexRateLine
               {:target [:page/id
                         ::page
                         ::rates
                         ::rates]})
     (dr/route-immediate (comp/get-ident IndexRatesPage {})))}
  (bulma/page
   (bulma/box
    (dom/h1 {}
            (tr [:index-rates "Index Rates"]))
    (dom/hr)
    (ui-index-rates rates))))

(def ui-page (comp/factory IndexRatesPage))
