(ns dinsro.ui.admin.ln.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/accounts.cljc]]
;; [[../../../model/ln/accounts.cljc]]
;; [[../../../ui/admin/ln.cljc]]

(def index-page-id :admin-ln-accounts)
(def model-key ::m.ln.accounts/id)
(def show-page-key :admin-ln-accounts-show)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-accounts-show)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.accounts/id
                        m.ln.accounts/node]
   ro/field-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "accounts"
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.accounts/index
   ro/title            "Lightning Accounts Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.accounts/keys [id name currency source wallet]
          :as               props}]
  {:ident             ::m.accounts/id
   :initial-state     {::m.accounts/name     ""
                       ::m.accounts/id       nil
                       ::m.accounts/currency {}
                       ::m.accounts/source   {}
                       ::m.accounts/wallet   {}}
   :query             [::m.accounts/name
                       ::m.accounts/id
                       {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                       {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                       {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str name))
        (dom/dl {}
          (dom/dt {} "Currency")
          (dom/dd {}
            (when currency
              (u.links/ui-currency-link currency)))
          (dom/dt {} "Source")
          (dom/dd {}
            (when source
              (u.links/ui-rate-source-link source)))
          (dom/dt {} "Wallet")
          (dom/dd {}
            (when wallet
              (u.links/ui-wallet-link wallet))))))
    (u.debug/load-error props "show account record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [id target]
          :as               props}]
  {:ident             (fn [] [::m.navlinks/id show-page-key])
   :initial-state     {::m.accounts/id     nil
                       ::m.navlinks/id     show-page-key
                       ::m.navlinks/target {}}
   :query             [::m.accounts/id
                       ::m.navlinks/id
                       {::m.navlinks/target (comp/get-query Show)}]
   :route-segment     ["account" :id]
   :will-enter        (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show account page")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Account"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
