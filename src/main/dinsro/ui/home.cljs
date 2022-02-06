(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.control-options :as copt]
   [com.fulcrologic.rad.container :as container :refer [defsc-container]]
   [com.fulcrologic.rad.container-options :as co]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.ln-nodes :as u.ln-nodes]))

(defsc-container HomePage2
  [_this _props]
  {:ident         (fn [] [:component/id ::HomePage])
   :initial-state {:component/id ::HomePage}
   :query         [:component/id]
   :route-segment [""]
   co/children    {:categories u.categories/CategoriesSubReport
                   :accounts   u.accounts/AccountsSubReport
                   :ln-nodes   u.ln-nodes/LNNodesSubReport}
   co/route       ""
   co/title       "Home Page"
   co/layout      [[{:id :categories :width 8} {:id :accounts :width 8}]
                   [{:id :ln-nodes :width 8}]]

   copt/controls       {::refresh {:type   :button
                                   :label  "Refresh"
                                   :action (fn [container] (control/run! container))}}
   copt/control-layout {:action-buttons [::refresh]}})

(defsc HomePage
  [_this _props]
  {:route-segment [""]
   :query []
   :initial-state {}
   :ident (fn [] [:component/id ::HomePage])}
  (dom/div {} "Home Page"))
