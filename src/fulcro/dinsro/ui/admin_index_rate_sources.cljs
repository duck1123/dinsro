(ns dinsro.ui.admin-index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc AdminIndexRateSources
  [_this {:keys [rate-sources]}]
  {:query [:rate-sources]
   :initial-state {:rate-sources []}}
  (dom/div
   (dom/h1
    (tr [:rate-sources])
    (dom/button "+"))
   (dom/div "create rate-source form")
   (if (empty? rate-sources)
     (dom/p (tr [:no-rate-sources]))
     (dom/table
      (dom/thead
       (dom/tr))))))
