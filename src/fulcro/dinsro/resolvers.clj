(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input #{::m.accounts/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (get sample/account-map id))

(defresolver account-map-resolver
  [_env _props]
  {::pc/output [::m.accounts/map]}
  {::m.accounts/map sample/account-map})

(defresolver accounts-resolver
  [_env _props]
  {::pc/output [{:all-accounts [::m.accounts/id]}]}
  {:all-accounts (map (fn [id] [::m.accounts/id id])
                      (keys sample/account-map))})

(defresolver auth-resolver
  [_env _props]
  {::pc/output [:auth/id]}
  {:auth/id 1})

(defresolver category-resolver
  [_env {::m.categories/keys [id]}]
  {::pc/input #{::m.categories/id}
   ::pc/output [::m.categories/name
                {::m.categories/user [::m.users/id]}]}
  (get sample/category-map id))

(defresolver category-map-resolver
  [_env _props]
  {::pc/output [::m.categories/map]}
  {::m.categories/map sample/category-map})

(defresolver currencies-resolver
  [_env _props]
  {::pc/output [{:all-currencies [::m.currencies/id]}]}
  {:all-currencies (map (fn [id] [::m.currencies/id id])
                        (keys sample/currency-map))})

(defresolver currency-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input #{::m.currencies/id}
   ::pc/output [::m.currencies/name]}
  (get sample/currency-map id))

(defresolver currency-map-resolver
  [_env _props]
  {::pc/output [::m.currencies/map]}
  {::m.currencies/map sample/currency-map})

(defresolver debug-menu-list-resolver
  [_env _props]
  {::pc/output [{:debug-menu/list [:debug-menu/id]}]}
  {:debug-menu/list
   (map
    (fn [id] [:debug-menu/id id])
    [:home
     :login
     :admin
     :accounts
     :categories
     :currencies
     :rate-sources
     :rates
     :transactions
     :users])})

(defresolver debug-menu-map-resolver
  [_env _props]
  {::pc/output [:debug-menu/map]}
  {:debug-menu/map sample/debug-menu-map})

(defresolver debug-menu-resolver
  [_env {:debug-menu/keys [id]}]
  {::pc/input #{:debug-menu/id}
   ::pc/output [:debug-menu/label
                :debug-menu/path]}
  {:debug-menu/label (get-in sample/debug-menu-map [id :debug-menu/label])
   :debug-menu/path (get-in sample/debug-menu-map [id :debug-menu/path])})

(defresolver debug-menus-resolver
  [_env _props]
  {::pc/output [{:all-debug-menus [:debug-menu/id]}]}
  {:all-debug-menus (map
                     (fn [id] [:debug-menu/id id])
                     (keys sample/debug-menu-map))})

(defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  (let [indexes (get env ::pc/indexes)]
    {:com.wsscode.pathom.viz.index-explorer/index
     (p/transduce-maps
      (remove (comp #{::pc/resolve ::pc/mutate} key))
      indexes)}))

(defresolver navlink-resolver
  [_env {:navlink/keys [id]}]
  {::pc/input #{:navlink/id}
   ::pc/output [:navlink/href
                :navlink/name]}
  (get sample/navlink-map id))

(defresolver navlink-map-resolver
  [_env _props]
  {::pc/output [:navlink/map]}
  {:navlink/map sample/navlink-map})

(defresolver rate-resolver
  [_env {::m.rates/keys [id]}]
  {::pc/input #{::m.rates/id}
   ::pc/output [{::m.rates/currency [::m.currencies/id]}
                ::m.rates/date
                ::m.rates/rate]}
  (get sample/rate-map id))

(defresolver rates-resolver
  [_env _props]
  {::pc/output [{:all-rates [::m.rates/id]}]}
  {:all-rates (map
               (fn [id] [::m.rates/id id])
               (keys sample/rate-map))})

(defresolver rate-map-resolver
  [_env _props]
  {::pc/output [::m.rates/map]}
  {::m.rates/map sample/rate-map})

(defresolver rate-source-resolver
  [_env {::m.rate-sources/keys [id]}]
  {::pc/input #{::m.rate-sources/id}
   ::pc/output [::m.rate-sources/name
                {::m.rate-sources/currency [::m.currencies/id]}
                ::m.rate-sources/url]}
  (get sample/rate-source-map id))

(defresolver rate-source-map-resolver
  [_env _props]
  {::pc/output [::m.rate-sources/map]}
  {::m.rate-sources/map sample/rate-source-map})

(defresolver rate-sources-resolver
  [_env _props]
  {::pc/output [{:all-rates [::m.rate-sources/id]}]}
  {:all-rates (map
               (fn [id] {::m.rate-sources/id id})
               (keys sample/rate-source-map))})

(defresolver transaction-resolver
  [_env {::m.transactions/keys [id]}]
  {::pc/input #{::m.transactions/id}
   ::pc/output [{::m.transactions/account [::m.accounts/id]}
                ::m.transactions/date
                ::m.transactions/description]}
  (get sample/transaction-map id))

(defresolver transactions-resolver
  [_env _props]
  {::pc/output [{:all-transactions [::m.transactions/id]}]}
  {:all-transactions
   (map (fn [id] [::m.transactions/id id]) (keys sample/transaction-map))})

(defresolver transaction-map-resolver
  [_env _props]
  {::pc/output [::m.transactions/map]}
  {::m.transactions/map sample/transaction-map})

(defresolver user-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input #{::m.users/id}
   ::pc/output [::m.users/email
                ::m.users/name]}
  (get sample/user-map id))

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/id]}]}
  {:all-users
   (map (fn [id] [::m.users/id id]) (keys sample/user-map))})

(defresolver user-map-resolver
  [_env _props]
  {::pc/output [::m.users/map]}
  {::m.users/map sample/user-map})

(def resolvers
  [account-resolver
   account-map-resolver
   accounts-resolver
   auth-resolver
   category-resolver
   category-map-resolver
   currencies-resolver
   currency-resolver
   currency-map-resolver
   debug-menu-resolver
   debug-menus-resolver
   debug-menu-list-resolver
   debug-menu-map-resolver
   ;; index-explorer
   navlink-resolver
   navlink-map-resolver
   rate-map-resolver
   rate-source-resolver
   rate-source-map-resolver
   rate-resolver
   rates-resolver
   transaction-resolver
   transactions-resolver
   transaction-map-resolver
   user-resolver
   users-resolver
   user-map-resolver])
