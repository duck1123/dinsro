(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.fulcro-css.css-injection :as inj]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.settings :as m.settings]
   [dinsro.mutations.settings :as mu.settings]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.core-address :as u.core-address]
   [dinsro.ui.core-block :as u.core-block]
   [dinsro.ui.core-nodes :as u.core-nodes]
   [dinsro.ui.core-peers :as u.core-peers]
   [dinsro.ui.core-tx :as u.core-tx]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.initialize :as u.initialize]
   [dinsro.ui.ln-channels :as u.ln-channels]
   [dinsro.ui.ln-invoices :as u.ln-invoices]
   [dinsro.ui.ln-nodes :as u.ln-nodes]
   [dinsro.ui.ln-payments :as u.ln-payments]
   [dinsro.ui.ln-payreqs :as u.ln-payreqs]
   [dinsro.ui.ln-peers :as u.ln-peers]
   [dinsro.ui.ln-transactions :as u.ln-tx]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.media :as u.media]
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.ui.rates :as u.rates]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.wallets :as u.wallets]
   [dinsro.ui.wallet-addresses :as u.wallet-addresses]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]))

(defrouter RootRouter
  [_this {:keys [current-state route-factory route-props]}]
  {:router-targets [u.accounts/AccountForm
                    u.accounts/AccountsReport
                    u.accounts/NewAccountForm
                    u.admin/AdminPage
                    u.categories/AdminCategoryForm
                    u.categories/CategoryForm
                    u.categories/CategoriesReport
                    u.core-address/CoreAddressForm
                    u.core-address/CoreAddressReport
                    u.core-block/CoreBlockForm
                    u.core-block/CoreBlockReport
                    u.core-nodes/NewCoreNodeForm
                    u.core-nodes/CoreNodeForm
                    u.core-nodes/CoreNodesReport
                    u.core-peers/CorePeersReport
                    u.core-tx/CoreTxForm
                    u.core-tx/CoreTxReport
                    u.currencies/AdminCurrencyForm
                    u.currencies/CurrencyForm
                    u.currencies/CurrenciesReport
                    u.home/HomePage
                    u.ln-channels/LNChannelForm
                    u.ln-channels/LNChannelsReport
                    u.ln-invoices/LNInvoiceForm
                    u.ln-invoices/LNInvoicesReport
                    u.ln-invoices/NewInvoiceForm
                    u.ln-nodes/LightningNodeForm
                    u.ln-nodes/LightningNodesReport
                    u.ln-payments/LNPaymentForm
                    u.ln-payments/LNPaymentsReport
                    u.ln-payreqs/NewPaymentForm
                    u.ln-payreqs/LNPaymentForm
                    u.ln-payreqs/LNPayreqsReport
                    u.ln-payreqs/NewPaymentForm
                    u.ln-peers/LNPeerForm
                    u.ln-peers/LNPeersReport
                    u.ln-tx/LNTransactionForm
                    u.ln-tx/LNTransactionsReport
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
                    u.wallet-addresses/WalletAddressesReport]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed  (dom/div "Failed!")
    ;; default will be used when the current state isn't yet set
    (dom/div {:style "height: 100%"}
      (dom/div "No route selected.")
      (when route-factory
        (comp/fragment
         (route-factory route-props))))))

(def ui-root-router (comp/factory RootRouter))

(defsc Root
  [this {:root/keys        [authenticator init-form navbar router]
         ::m.settings/keys [site-config]}]
  {:componentDidMount
   (fn [this]
     (df/load! this ::m.settings/site-config mu.settings/Config)

     (df/load! this :root/navbar u.navbar/NavbarUnion)

     (uism/begin! this machines/hideable ::u.navbar/navbarsm
                  {:actor/navbar
                   (uism/with-actor-class [::m.navbar/id :main]
                     u.navbar/Navbar)}))
   :query
   [{:root/authenticator (comp/get-query u.authenticator/Authenticator)}
    {:root/navbar (comp/get-query u.navbar/Navbar)}
    {:root/init-form (comp/get-query u.initialize/InitForm)}
    {:root/router (comp/get-query RootRouter)}
    ::auth/authorization
    {::m.settings/site-config (comp/get-query mu.settings/Config)}]
   :initial-state {:root/navbar             {}
                   :root/authenticator      {}
                   :root/init-form          {}
                   :root/router             {}
                   ::m.settings/site-config {}}}
  (let [inverted                                   true
        visible                                    (= (uism/get-active-state this ::u.navbar/navbarsm) :state/shown)
        top-router-state                           (or (uism/get-active-state this ::RootRouter) :initial)
        {::m.settings/keys [loaded? initialized?]} site-config
        root                                       (uism/get-active-state this ::auth/auth-machine)
        gathering-credentials?                     (#{:state/gathering-credentials} root)]
    (comp/fragment
     (u.media/ui-media-styles)
     (u.media/ui-media-context-provider
      {}
      (if loaded?
        (if initialized?
          (dom/div {:style {:height "100%"}}
            (when navbar
              (u.navbar/ui-navbar navbar))
            (ui-sidebar-pushable
             {:inverted (str inverted)
              :visible  (str visible)}
             (when navbar
               (u.navbar/ui-navbar-sidebar navbar))
             (ui-sidebar-pusher
              {:style {:paddingTop "12px"}}
              (if (= :initial top-router-state)
                (dom/div :.loading "Loading...")
                (comp/fragment
                 (u.authenticator/ui-authenticator authenticator)
                 (when-not gathering-credentials?
                   (ui-root-router router)))))))
          (u.initialize/ui-init-form init-form))
        (dom/p "Not loaded")))
     (inj/style-element {:component Root}))))

(def ui-root (comp/factory Root))
