(ns dinsro.views.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc HomePage
  [_this _props]
  (dom/div
   (dom/h1 "Home Page")))

(def ui-page (comp/factory HomePage))
