(ns dinsro.ui.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]))

(defsc Show
  [_this {::m.debits/keys [value]}]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/value 0
                   ::m.debits/id    nil}
   :pre-merge     (u.links/page-merger ::m.debits/id {})
   :query         [::m.debits/value
                   ::m.debits/id]
   :route-segment ["debits" :id]
   :will-enter    (partial u.links/page-loader ::m.debits/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Debit " (str value)))))
