(ns dinsro.ui.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc IndexRateSourceLine
  [_this {:index-rate-source-line/keys [name url currency-id]}]
  {:initial-state (fn [_] {:index-rate-source-line/name "sally"
                           :index-rate-source-line/url "sally"})}
  (dom/tr
   (dom/td name)
   (dom/td url)
   (dom/td currency-id
           #_(c.links/currency-link currency-id))
   (dom/td "Delete"
           #_(c.buttons/delete-rate-source item))))

(def ui-index-rate-source-line (comp/factory IndexRateSourceLine))

(defsc IndexRateSources
  [_this {:index-rate-sources/keys [data]}]
  {:query [:index-rate-sources/data]
   :initial-state (fn [_] {:index-rate-sources/data []})}
  (dom/div
   ;; (dom/p "Index Rate Sources")
   (dom/table
    (dom/thead
     (dom/tr
      (dom/th (tr [:name]))
      (dom/th (tr [:url]))
      (dom/th (tr [:currency]))
      (dom/th (tr [:actions]))))
    (dom/tbody
     (map ui-index-rate-source-line data)))))
