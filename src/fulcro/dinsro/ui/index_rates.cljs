(ns dinsro.ui.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexRateLine
  [_this {::m.rates/keys [currency date rate]}]
  {:ident ::m.rates/id
   :initial-state {::m.rates/currency 0
                   ::m.rates/date ""
                   ::m.rates/id 0
                   ::m.rates/rate 0}
   :query [::m.rates/id
           ::m.rates/currency
           ::m.rates/date
           ::m.rates/rate]}
  (dom/tr
   (dom/td currency)
   (dom/td date)
   (dom/td rate)
   (dom/td
    (dom/button :.button.is-danger "Delete")
    #_(c.buttons/delete-rate-source item))))

(def ui-index-rate-source-line (comp/factory IndexRateLine {:keyfn ::m.rates/id}))

(defsc IndexRates
  [_this {::keys [rates]}]
  {:initial-state {::rates []}
   :query [{::rates (comp/get-query IndexRateLine)}]}
  (if (seq rates)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:currency]))
       (dom/th (tr [:date]))
       (dom/th (tr [:rate]))
       (dom/th (tr [:actions]))))
     (dom/tbody
      (map ui-index-rate-source-line rates)))
    (dom/p "no items")))

(def ui-index-rates (comp/factory IndexRates))
