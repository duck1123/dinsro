(ns dinsro.views.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc IndexRateSourcesPage
  [_this _props]
  (dom/div "Index rate sources"))

(def ui-page (comp/factory IndexRateSourcesPage))
