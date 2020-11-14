(ns dinsro.ui.filters
  (:require
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn filter-page
  [page]
  #(when (= (get-in % [:data :name]) page) true))

(defn filter-param-page
  [page]
  (fn [match]
    (when (= (get-in match [:data :name]) page)
      (:path-params match))))
