(ns dinsro.ui.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rates :as m.rates]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexRateLine
  [_this {::m.rates/keys [currency date rate]}]
  {:query [::m.rates/id
           ::m.rates/currency
           ::m.rates/date
           ::m.rates/rate]
   :ident ::m.rates/id
   :initial-state
   (fn [{::m.rates/keys [id]}]
     (get sample/rate-map id {::m.rates/id id
                              ::m.rates/currency 1
                              ::m.rates/rate 1.001
                              ::m.rates/date "2020-12-12 13:52:40"}))}
  (dom/tr
   (dom/td currency)
   (dom/td date)
   (dom/td rate)
   (dom/td
    (dom/button :.button.is-danger "Delete")
    #_(c.buttons/delete-rate-source item))))

(def ui-index-rate-source-line (comp/factory IndexRateLine {:keyfn ::m.rates/id}))

(defsc IndexRates
  [_this {:rates/keys [items]}]
  {:query [{:rates/items (comp/get-query IndexRateLine)}]
   :initial-state
   (fn [_]
     (let [ids [1 2]]
       {:rates/items (map #(comp/get-initial-state IndexRateLine {::m.rates/id %}) ids)}))}
  (if (seq items)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:currency]))
       (dom/th (tr [:date]))
       (dom/th (tr [:rate]))
       (dom/th (tr [:actions]))))
     (dom/tbody
      (map ui-index-rate-source-line items)))
    (dom/p "no items")))

(def ui-index-rates (comp/factory IndexRates))
