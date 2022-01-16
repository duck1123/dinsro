(ns dinsro.model.navlink
  (:refer-clojure :exclude [name])
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.wsscode.pathom.connect :as pc]
   [taoensso.timbre :as log]))

(comment ::pc/_)

(def links
  {:accounts        ["Accounts"          "/accounts"         :dinsro.ui.accounts/AccountsReport]
   :admin           ["Admin"             "/admin/users"      :dinsro.ui.admin/AdminPage]
   :categories      ["Categories"        "/categories"       :dinsro.ui.categories/CategoriesReport]
   :channels        ["Channels"          "/ln-channels"      :dinsro.ui.ln-channels/LNChannelsReport]
   :core-nodes      ["Core Nodes"        "/core-nodes"       :dinsro.ui.core-nodes/CoreNodesReport]
   :currencies      ["Currencies"        "/currencies"       :dinsro.ui.currencies/CurrenciesReport]
   :home            ["Home"              "/"                 :dinsro.ui.home/HomePage2]
   :lightning-nodes ["Lightning Nodes"   "/ln-nodes"         :dinsro.ui.ln-nodes/LightningNodesReport]
   :login           ["Login"             "/login"            :dinsro.ui.login/LoginPage]
   :peers           ["Peers"             "/ln-peers"         :dinsro.ui.ln-peers/LNPeersReport]
   :rates           ["Rates"             "/rates"            :dinsro.ui.rates/RatesReport]
   :rate-sources    ["Rate Sources"      "/rate-sources"     :dinsro.ui.rate-sources/RateSourcesReport]
   :registration    ["Registration"      "/register"         :dinsro.ui.registration/RegistrationPage]
   :settings        ["Settings"          "/settings"         :dinsro.ui.settings/SettingsPage]
   :transactions    ["Transactions"      "/transactions"     :dinsro.ui.transactions/TransactionsReport]
   :tx              ["LN TXes"           "/ln-transactions"  :dinsro.ui.ln-transactions/LNTransactionsReport]
   :users           ["Users"             "/users"            :dinsro.ui.users/UsersReport]})

#?(:clj
   (pc/defresolver id
     [_env _props]
     {ao/pc-output  [::id]
      ao/pc-resolve (fn [_env _props] {})}))

(defattr name ::name :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::name]
   ao/pc-resolve (fn [_env {::keys [id]}] {::name (nth (get links id) 0)})})

(defattr href ::href :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::href]
   ao/pc-resolve (fn [_env {::keys [id]}] {::href (nth (get links id) 1)})})

(defattr target ::target :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::target]
   ao/pc-resolve (fn [_env {::keys [id]}] {::target (nth (get links id) 2)})})

(defattr auth-link? ::auth-link? :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::auth-link?]
   ao/pc-resolve (fn [_env {::keys [id]}] {::auth-link? (not (nil? (#{:login} id)))})})

(defattr index ::index :ref
  {ao/target    ::id
   ao/pc-output [{::index [::id]}]
   ao/pc-resolve
   (fn [_env _props] {::index (map (fn [id] {::id id}) (keys links))})})

(defn idents
  [ids]
  (map (fn [id] {::id id}) ids))

(def attributes
  [name
   href
   target
   auth-link?
   index])

#?(:clj (def resolvers [id]))
