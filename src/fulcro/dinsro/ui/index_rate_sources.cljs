(ns dinsro.ui.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def default-name "sally")
(def default-url "https://example.com/")

(defsc IndexRateSourceLine
  [_this {::m.rate-sources/keys [name url currency-id]}]
  {:query [::m.rate-sources/id
           ::m.rate-sources/currency-id
           ::m.rate-sources/name
           ::m.rate-sources/url]
   :ident ::m.rate-sources/id
   :initial-state (fn [_] {::m.rate-sources/name "sally"
                           ::m.rate-sources/url "sally"})}
  (dom/tr
   (dom/td name)
   (dom/td url)
   (dom/td currency-id
           #_(c.links/currency-link currency-id))
   (dom/td
    (dom/button :.button.is-danger "Delete")
    #_(c.buttons/delete-rate-source item))))

(def ui-index-rate-source-line (comp/factory IndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc IndexRateSources
  [_this {:index-rate-sources/keys [items]}]
  {:query [:index-rate-sources/items
           {:index-rate-source-line/rate-data (comp/get-query IndexRateSourceLine)}]
   :initial-state (fn [_] {:index-rate-sources/data []
                           :index-rate-sources/items []})}
  (dom/table
   :.table
   (dom/thead
    (dom/tr
     (dom/th (tr [:name]))
     (dom/th (tr [:url]))
     (dom/th (tr [:currency]))
     (dom/th (tr [:actions]))))
   (dom/tbody
    (map ui-index-rate-source-line items))))
