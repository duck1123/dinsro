(ns dinsro.views.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoriesPage
  [_this _props]
  (dom/div "Index categories"))

(def ui-page (comp/factory IndexCategoriesPage))
