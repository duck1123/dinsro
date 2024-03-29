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
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.ln.accounts :as o.ln.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/accounts.cljc]]
;; [[../../../model/ln/accounts.cljc]]
;; [[../../../ui/admin/ln.cljc]]

(def index-page-id :admin-ln-accounts)
(def model-key o.ln.accounts/id)
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
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.accounts/index
   ro/title            "Lightning Accounts Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.accounts/keys [id name currency source wallet]
          :as               props}]
  {:ident         ::m.accounts/id
   :initial-state (fn [_props]
                    {o.accounts/name     ""
                     o.accounts/id       nil
                     o.accounts/currency (comp/get-initial-state u.links/CurrencyLinkForm)
                     o.accounts/source   (comp/get-initial-state u.links/RateSourceLinkForm)
                     o.accounts/wallet   (comp/get-initial-state u.links/WalletLinkForm)})
   :query         (fn []
                    [o.accounts/name
                     o.accounts/id
                     {o.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                     {o.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                     {o.accounts/wallet (comp/get-query u.links/WalletLinkForm)}])}
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
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["account" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Accounts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Account"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
