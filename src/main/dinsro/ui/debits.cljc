(ns dinsro.ui.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../model/debits.cljc]]
;; [[../joins/debits.cljc]]

(def model-key ::m.debits/id)
(def parent-router-id :root)
(def show-page-id :debits-show)
(def show-props? false)
(def required-role :user)

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
                   ::j.debits/event-value]}
  (dom/div {}
    (ui-segment {}
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
      (when show-props?
        (dom/div {} (u.debug/ui-props-logger props)))
      (dom/div {} (str positive?)))))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.debits/id       nil
                   ::m.navlinks/id     show-page-id
                   ::m.navlinks/target {}}
   :query         [::m.debits/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["debit" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (ui-show target))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Node"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
