(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks :refer [defroute]]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.accounts.transactions :as u.a.transactions]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.accounts :as u.f.accounts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.accounts :as u.r.accounts]
   [lambdaisland.glogc :as log]))

;; [[../joins/accounts.cljc]]
;; [[../model/accounts.cljc]]
;; [[../mutations/accounts.cljc]]
;; [[../options/accounts.cljc]]

(def index-page-id :accounts)
(def index-page-segment "accounts")
(def model-key o.accounts/id)
(def parent-router-id :root)
(def show-page-id :accounts-show)

(def show-transactions true)
(def use-form2? false)

(defsc Show
  [_this {::m.accounts/keys [id name currency source wallet]
          :ui/keys          [transactions]
          :as               props}]
  {:componentDidMount #(report/start-report! % u.a.transactions/Report {:route-params (comp/props %)})
   :ident             ::m.accounts/id
   :initial-state     {::m.accounts/name     ""
                       ::m.accounts/id       nil
                       ::m.accounts/currency {}
                       ::m.accounts/source   {}
                       ::m.accounts/wallet   {}
                       :ui/transactions      {}}
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/transactions [u.a.transactions/Report {}]})
   :query             [::m.accounts/name
                       ::m.accounts/id
                       {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                       {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                       {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                       {:ui/transactions (comp/get-query u.a.transactions/Report)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {}
          (dom/span {}
            (str name))
          (when currency
            (dom/span {}
              (dom/span {} "(")
              (u.links/ui-currency-link currency)
              (dom/span {} ")"))))
        (dom/dl {}
          (dom/dt {} "Source")
          (dom/dd {}
            (when source
              (u.links/ui-rate-source-link source)))
          (when wallet
            (comp/fragment
             (dom/dt {} "Wallet")
             (dom/dd {}
               (u.links/ui-wallet-link wallet))))))
      (when show-transactions
        (comp/fragment
         (ui-segment {}
           (dom/h2 "Transactions"))
         (ui-segment {}
           (if transactions
             (u.a.transactions/ui-report transactions)
             (dom/div {}
               (dom/p {} "No Transactions")))))))
    (u.debug/load-error props "show account record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [form form2 report]
          :as      props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (log/info :IndexPage/did-mount {:this this
                                                          :props props})
                          (report/start-report! this u.r.accounts/Report {})))
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/form      (comp/get-initial-state u.f.accounts/InlineForm {})
                         :ui/form2     (or (comp/get-initial-state u.f.accounts/InlineForm-form {}) {})
                         :ui/report    (comp/get-initial-state u.r.accounts/Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/form (comp/get-query u.f.accounts/InlineForm {})}
                         {:ui/form2 (comp/get-query u.f.accounts/InlineForm-form {})}
                         {:ui/report (comp/get-query u.r.accounts/Report {})}])
   :route-segment     [index-page-segment]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if-not use-form2?
      (u.f.accounts/ui-inline-form form)
      (if form2
        (u.f.accounts/ui-inline-form-form form2)
        (dom/div {} "No Form")))
    (u.r.accounts/ui-report report)
    #_(u.debug/log-props props)
    #_(u.debug/log-props (comp/get-initial-state InlineForm-form))
    #_(u.debug/log-list (or (comp/get-query InlineForm-form) {}))))

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

(defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Accounts"
   o.navlinks/description   "An index of all accounts for a user"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role :user})

(defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Show page for an account"
   o.navlinks/label         "Show Accounts"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role :user})
