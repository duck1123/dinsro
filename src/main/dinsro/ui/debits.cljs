(ns dinsro.ui.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]))

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.debits/value
                        m.debits/account
                        m.debits/transaction]
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.debits/account #(u.links/ui-account-link %2)
                        ::m.debits/transaction #(u.links/ui-transaction-link %2)}
   ro/route            "debits"
   ro/row-actions      []
   ro/row-pk           m.debits/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.debits/admin-index
   ro/title            "Admin Debits Report"})

(defsc ShowDebits
  [_this {::m.debits/keys [value]}]
  {:route-segment ["debits" :id]
   :query         [::m.debits/value
                   ::m.debits/id]
   :initial-state {::m.debits/value 0
                   ::m.debits/id    nil}
   :ident         ::m.debits/id
   :will-enter    (partial u.links/page-loader ::m.debits/id ::ShowDebit)
   :pre-merge     (u.links/page-merger
                   ::m.debits/id
                   {})}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Debit " (str value)))))
