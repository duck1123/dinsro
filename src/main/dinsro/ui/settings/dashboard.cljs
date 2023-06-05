(ns dinsro.ui.settings.dashboard
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]))

(defsc Page
  [_this _props]
  {:ident          (fn [] [:page/id ::Dashboard])
   :initial-state  {}
   :query          []
   :route-segment  ["dashboard"]}
  (ui-grid {}
    (ui-grid-row {:centered true}
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (dom/div :.ui.segment
            (dom/h1 "Settings"))))
      (ui-grid-column {:computer 8 :tablet 8 :mobile 16}
        (ui-container {}
          (dom/div :.ui.segment
            (dom/h2 "Core Nodes")))))))
