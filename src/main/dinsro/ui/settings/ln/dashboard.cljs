(ns dinsro.ui.settings.ln.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Dashboard
  [_this _props]
  {:ident         (fn [] [:component/id ::Dashboard])
   :initial-state {:component/id ::Dashboard}
   :query         [:component/id]
   :route-segment ["dashboard"]}
  (dom/div :.ui.segment
    (dom/h1 {} "LN Dashboard")))
