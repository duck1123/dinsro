(ns dinsro.ui
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro-css.css-injection :as inj]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.message.ui-message :refer [ui-message]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.settings :as m.settings]
   [dinsro.mutations.ui :as mu.ui]
   [dinsro.mutations.navbar :as mu.navbar]
   [dinsro.mutations.settings :as mu.settings]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.core.addresses :as u.core-addresses]
   [dinsro.ui.core.blocks :as u.core-blocks]
   [dinsro.ui.core.nodes :as u.core-nodes]
   [dinsro.ui.core.peers :as u.core-peers]
   [dinsro.ui.core.tx :as u.core-tx]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.initialize :as u.initialize]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.payments :as u.ln.payments]
   [dinsro.ui.ln.payreqs :as u.ln.payreqs]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [dinsro.ui.ln.transactions :as u.ln.tx]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.ui.rates :as u.rates]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.users :as u.users]
   [dinsro.ui.core.wallets :as u.wallets]
   [dinsro.ui.core.wallet-addresses :as u.wallet-addresses]
   [dinsro.ui.core.words :as u.words]
   ["fomantic-ui"]))

(defsc GlobalErrorDisplay [this {:ui/keys [global-error]}]
  {:query         [[:ui/global-error '_]]
   :ident         (fn [] [:component/id :GlobalErrorDisplay])
   :initial-state {}}
  (when global-error
    (ui-message
     {:content   (str "Something went wrong: " global-error)
      :error     true
      :onDismiss #(comp/transact!! this [(mu.ui/reset-global-error {})])})))

(def ui-global-error-display (comp/factory GlobalErrorDisplay))

(defrouter RootRouter
  [_this {:keys [current-state route-factory route-props]}]
  {:css            [[:.rootrouter {:height "100%"}]]
   :router-targets [u.accounts/AccountForm
                    u.accounts/AccountsReport
                    u.accounts/NewAccountForm
                    u.admin/AdminPage
                    u.categories/AdminCategoryForm
                    u.categories/CategoryForm
                    u.categories/CategoriesReport
                    u.categories/NewCategoryForm
                    u.core-addresses/CoreAddressForm
                    u.core-addresses/CoreAddressReport
                    u.core-blocks/CoreBlockForm
                    u.core-blocks/CoreBlockReport
                    u.core-nodes/NewCoreNodeForm
                    u.core-nodes/CoreNodeForm
                    u.core-nodes/CoreNodesReport
                    u.core-peers/CorePeerForm
                    u.core-peers/CorePeersReport
                    u.core-peers/NewCorePeerForm
                    u.core-tx/CoreTxForm
                    u.core-tx/CoreTxReport
                    u.currencies/AdminCurrencyForm
                    u.currencies/CurrencyForm
                    u.currencies/CurrenciesReport
                    u.currencies/NewCurrencyForm
                    u.home/HomePage
                    u.ln.channels/LNChannelForm
                    u.ln.channels/LNChannelsReport
                    u.ln.invoices/LNInvoiceForm
                    u.ln.invoices/LNInvoicesReport
                    u.ln.invoices/NewInvoiceForm
                    u.ln.nodes/CreateLightningNodeForm
                    u.ln.nodes/LightningNodeForm
                    u.ln.nodes/LightningNodesReport
                    u.ln.payments/LNPaymentForm
                    u.ln.payments/LNPaymentsReport
                    u.ln.payreqs/NewPaymentForm
                    u.ln.payreqs/LNPaymentForm
                    u.ln.payreqs/LNPayreqsReport
                    u.ln.payreqs/NewPaymentForm
                    u.ln.peers/LNPeerForm
                    u.ln.peers/LNPeersReport
                    u.ln.tx/LNTransactionForm
                    u.ln.tx/LNTransactionsReport
                    u.login/LoginPage
                    u.rate-sources/RateSourceForm
                    u.rate-sources/RateSourcesReport
                    u.rates/RateForm
                    u.rates/RatesReport
                    u.registration/RegistrationPage
                    u.transactions/TransactionForm
                    u.transactions/TransactionsReport
                    u.users/AdminUserForm
                    u.users/UserForm
                    u.users/UsersReport
                    u.wallets/NewWalletForm
                    u.wallets/WalletForm
                    u.wallets/WalletReport
                    u.wallet-addresses/NewWalletAddressForm
                    u.wallet-addresses/WalletAddressForm
                    u.wallet-addresses/WalletAddressesReport
                    u.words/WordReport]}
  (let [{:keys [rootrouter]} (css/get-classnames RootRouter)]
    (case current-state
      :pending (dom/div "Loading...")
      :failed  (dom/div "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {:classes [rootrouter]}
        (dom/div "No route selected.")
        (when route-factory
          (comp/fragment
           (route-factory route-props)))))))

(def ui-root-router (comp/factory RootRouter))

(defsc Root
  [this {:root/keys        [authenticator global-error init-form navbar router]
         ::m.settings/keys [site-config]}]
  {:componentDidMount
   (fn [this]
     (df/load! this ::m.settings/site-config mu.settings/Config)

     (df/load! this :root/navbar u.navbar/NavbarUnion)

     (uism/begin! this machines/hideable ::mu.navbar/navbarsm
                  {:actor/navbar
                   (uism/with-actor-class [::m.navbar/id :main]
                     u.navbar/Navbar)}))
   :css           [[:.pushable {:border "1px solid blue"}]
                   [:.pusher {:border   "1px solid green"
                              :height   "100%"
                              :overflow "auto !important"}]
                   [:.top {:height     "100%"
                           :margin-top "32px"}]]
   :query
   [{:root/authenticator (comp/get-query u.authenticator/Authenticator)}
    {:root/navbar (comp/get-query u.navbar/Navbar)}
    {:root/init-form (comp/get-query u.initialize/InitForm)}
    {:root/global-error (comp/get-query GlobalErrorDisplay)}
    {:root/router (comp/get-query RootRouter)}
    ::auth/authorization
    {::m.settings/site-config (comp/get-query mu.settings/Config)}]
   :initial-state {:root/navbar             {}
                   :root/authenticator      {}
                   :root/init-form          {}
                   :root/router             {}
                   :root/global-error       {}
                   ::m.settings/site-config {}}}
  (let [{:keys [pushable pusher top]} (css/get-classnames Root)
        top-router-state              (or (uism/get-active-state this ::RootRouter) :initial)
        {::m.settings/keys
         [loaded? initialized?]}      site-config
        root                          (uism/get-active-state this ::auth/auth-machine)
        gathering-credentials?        (#{:state/gathering-credentials} root)]
    (comp/fragment
     (if loaded?
       (if initialized?
         (comp/fragment
          (u.navbar/ui-navbar navbar)
          (dom/div {:classes [top]}
            (ui-sidebar-pushable
             {:className (string/join " " [pushable])}
             (u.navbar/ui-navbar-sidebar navbar)
             (ui-sidebar-pusher
              {:className (string/join " " [pusher])}
              (if (= :initial top-router-state)
                (dom/div :.loading "Loading...")
                (comp/fragment
                 (ui-global-error-display global-error)
                 (u.authenticator/ui-authenticator authenticator)
                 (when-not gathering-credentials?
                   (ui-root-router router))))))))
         (u.initialize/ui-init-form init-form))
       (dom/div {}
         (dom/p "Not loaded")))
     (inj/style-element {:component Root}))))

(def ui-root (comp/factory Root))
