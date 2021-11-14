(ns dinsro.model.navlink
  (:refer-clojure :exclude [name])
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.wsscode.pathom.connect :as pc]
   [taoensso.timbre :as log]))

(comment ::pc/_)

(def links
  {:accounts        ["Accounts"        "/accounts"        :dinsro.ui.accounts/AccountsReport]
   :admin           ["Admin"           "/admin"           :dinsro.ui.admin/AdminPage]
   :categories      ["Categories"      "/categories"      :dinsro.ui.categories/CategoriesReport]
   :channels        ["Channels"        "/ln-channels"     :dinsro.ui.ln-channels/LNChannelsReport]
   :core-nodes      ["Core Nodes"      "/core-nodes"      :dinsro.ui.core-nodes/CoreNodesReport]
   :currencies      ["Currencies"      "/currencies"      :dinsro.ui.currencies/CurrenciesReport]
   :home            ["Home"            "/"                :dinsro.ui.home/HomePage2]
   :lightning-nodes ["Lightning Nodes" "/ln-nodes"        :dinsro.ui.ln-nodes/LightningNodesReport]
   :login           ["Login"           "/login"           :dinsro.ui.login/LoginPage]
   :peers           ["Peers"           "/ln-peers"        :dinsro.ui.ln-peers/LNPeersReport]
   :rates           ["Rates"           "/rates"           :dinsro.ui.rates/RatesReport]
   :rate-sources    ["Rate Sources"    "/rate-sources"    :dinsro.ui.rate-sources/RateSourcesReport]
   :registration    ["Registration"    "/register"        :dinsro.ui.registration/RegistrationPage]
   :settings        ["Settings"        "/settings"        :dinsro.ui.settings/SettingsPage]
   :transactions    ["Transactions"    "/transactions"    :dinsro.ui.transactions/TransactionsReport]
   :tx              ["LN TXes"         "/ln-transactions" :dinsro.ui.ln-transactions/LNTransactionsReport]
   :users           ["Users"            "/users"           :dinsro.ui.users/UsersReport]})

(def auth-link-names [:accounts])
(def dropdown-link-names
  [:users
   :currencies
   :categories
   :rates
   :rate-sources
   :tx
   :peers
   :lightning-nodes
   :core-nodes
   :transactions
   :accounts
   :admin])
(def menu-link-names [:accounts :transactions])
(def unauth-link-names [:login :register])

#?(:clj
   (pc/defresolver id
     [_env _props]
     {ao/pc-output  [::id]
      ao/pc-resolve (fn [_env _props] {})}))

(defattr name ::name :string
  {ao/identities #{::id}
   ao/pc-input #{::id}
   ao/pc-output [::name]
   ao/pc-resolve (fn [_env {::keys [id]}] {::name (nth (get links id) 0)})})

(defattr href ::href :string
  {ao/identities #{::id}
   ao/pc-input #{::id}
   ao/pc-output [::href]
   ao/pc-resolve (fn [_env {::keys [id]}] {::href (nth (get links id) 1)})})

(defattr target ::target :keyword
  {ao/identities #{::id}
   ao/pc-input #{::id}
   ao/pc-output [::target]
   ao/pc-resolve (fn [_env {::keys [id]}] {::target (nth (get links id) 2)})})

(defattr all-navlinks ::all-navlinks :ref
  {ao/target    ::id
   ao/pc-output [{::all-navlinks [::id]}]
   ao/pc-resolve
   (fn [_env _props] {::all-navlinks (map (fn [id] {::id id}) (keys links))})})

(defattr navbar-id :navbar/id :symbol
  {ao/identity? true})

(defattr auth-links ::auth-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::auth-links [::id]}]
   ao/pc-resolve (fn [_env _props] {::auth-links (map (fn [id] {::id id}) auth-link-names)})})

(defattr dropdown-links ::dropdown-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::dropdown-links [::id]}]
   ao/pc-resolve (fn [_env _props] {::dropdown-links (map (fn [id] {::id id}) dropdown-link-names)})})

(defattr menu-links ::menu-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::menu-links [::id]}]
   ao/pc-resolve (fn [_env _props] {::menu-links (map (fn [id] {::id id}) menu-link-names)})})

(defattr unauth-links ::unauth-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::unauth-links [::id]}]
   ao/pc-resolve (fn [_env _] {::unauth-links (map (fn [id] {::id id}) unauth-link-names)})})

(defattr current-navbar ::current-navbar :ref
  {ao/target     :navbar/id

   ao/pc-output  [{::current-navbar [:navbar/id]}]
   ao/pc-resolve (fn [_ _] {::current-navbar {:navbar/id :main}})})

(defattr current-navbar-navbar :dinsro.ui/navbar :ref
  {ao/target     :navbar/id
   ao/pc-output  [{:dinsro.ui/navbar [:navbar/id]}]
   ao/pc-resolve (fn [_ _] {:dinsro.ui/navbar {:navbar/id :main}})})

(defattr current-navbar-sidebar :dinsro.ui/sidebar :ref
  {ao/target     :navbar/id
   ao/pc-output  [{:dinsro.ui/sidebar [:navbar/id]}]
   ao/pc-resolve (fn [_ _] {:dinsro.ui/sidebar {:navbar/id :main}})})

(def attributes
  [name
   href
   target
   navbar-id
   current-navbar
   current-navbar-navbar
   current-navbar-sidebar
   all-navlinks
   dropdown-links
   menu-links
   unauth-links])

#?(:clj (def resolvers [id]))
