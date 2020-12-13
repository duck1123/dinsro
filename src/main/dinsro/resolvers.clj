(ns dinsro.resolvers
  (:require
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

(def people-table
  (atom
   {1 {:person/id 1 :person/name "Sally" :person/age 32}
    2 {:person/id 2 :person/name "Joe" :person/age 22}
    3 {:person/id 3 :person/name "Fred" :person/age 11}
    4 {:person/id 4 :person/name "Bobby" :person/age 55}}))

(def list-table
  (atom
   {:friends {:list/id     :friends
              :list/label  "Friends"
              :list/people [1 2]}
    :enemies {:list/id     :enemies
              :list/label  "Enemies"
              :list/people [4 3]}}))

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

(defresolver all-lists-resolver [_env _props]
  {::pc/output [:all-lists]}
  {:all-lists [{:list/id :friends}
               {:list/id :enemies}]})

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

(defresolver foo-resolver [_env _props]
  {::pc/output [:foo/name]}
  {:foo/name "Foo Name"})

(pc/defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (get env ::pc/indexes)})

;; Given a :list/id, this can generate a list label and the people
;; in that list (but just with their IDs)
(defresolver list-resolver [_env {:list/keys [id]}]
  {::pc/input  #{:list/id}
   ::pc/output [:list/label {:list/people [:person/id]}]}
  (when-let [list (get @list-table id)]
    (assoc list
           :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

;; Given :person/id, this can generate the details of a person
(defresolver person-resolver [_env {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:person/name :person/age]}
  (get @people-table id))

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
   all-lists-resolver
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
   foo-resolver
   ;; index-explorer
   list-resolver
   navlink-resolver
   navlink-map-resolver
   person-resolver
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
