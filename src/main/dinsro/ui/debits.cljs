(ns dinsro.ui.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]))

(defsc Show
  [_this {::m.debits/keys [value]}]
  {:route-segment ["debits" :id]
   :query         [::m.debits/value
                   ::m.debits/id]
   :initial-state {::m.debits/value 0
                   ::m.debits/id    nil}
   :ident         ::m.debits/id
   :will-enter    (partial u.links/page-loader ::m.debits/id ::Show)
   :pre-merge     (u.links/page-merger ::m.debits/id {})}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Debit " (str value)))))
