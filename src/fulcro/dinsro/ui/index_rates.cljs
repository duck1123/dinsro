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
   :initial-state (fn [_] {::m.rates/name "sally"
                           ::m.rates/url "sally"})}
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
  {:query [:rates/items
           {:rates/rate-data (comp/get-query IndexRateLine)}]
   :initial-state (fn [_]
                    {:rates/data []
                     :rates/items (vals sample/rate-map)})}
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