(ns dinsro.ui.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [../model/debits.cljc]
;; [../joins/debits.cljc]

(def show-props false)

(defsc Show
  [_this {::m.debits/keys [value transaction account]
          ::j.debits/keys [currency current-rate positive? current-rate-value
                           event-value]
          :as             props}]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/value              0
                   ::m.debits/id                 nil
                   ::m.debits/account            {}
                   ::m.debits/transaction        {}
                   ::j.debits/currency           {}
                   ::j.debits/current-rate       {}
                   ::j.debits/positive?          true
                   ::j.debits/current-rate-value 0
                   ::j.debits/event-value        0}
   :pre-merge     (u.loader/page-merger ::m.debits/id {})
   :query         [::m.debits/value
                   ::m.debits/id
                   ::j.debits/positive?
                   {::m.debits/account (comp/get-query u.links/AccountLinkForm)}
                   {::m.debits/transaction (comp/get-query u.links/TransactionLinkForm)}
                   {::j.debits/currency (comp/get-query u.links/CurrencyLinkForm)}
                   {::j.debits/current-rate (comp/get-query u.links/RateValueLinkForm)}
                   ::j.debits/current-rate-value
                   ::j.debits/event-value]
   :route-segment ["debits" :id]
   :will-enter    (partial u.loader/page-loader ::m.debits/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Debit " (str value))
     (dom/div {} (u.links/ui-currency-link currency))
     (dom/div {} (u.links/ui-account-link account))
     (dom/div {} (u.links/ui-transaction-link transaction))
     (dom/div {}
       (str "\"" event-value "\" - "))

     (dom/div {}
       (str "\"" current-rate-value "\" - ")
       "Current Rate: "
       (if current-rate
         (u.links/ui-rate-value-link current-rate)
         "Missing"))
     (when show-props (dom/div {} (u.debug/log-props props)))
     (dom/div {} (str positive?)))))
