(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc HomePage
  [_this _props]
  {:ident         (fn [] [:page/id ::HomePage])
   :initial-state {:page/id ::HomePage}
   :query         [:page/id]
   :route-segment [""]}
  (dom/div {}
    (dom/h1 :.title (tr [:home-page]))))
