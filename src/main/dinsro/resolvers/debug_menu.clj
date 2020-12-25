(ns dinsro.resolvers.debug-menu
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver debug-menu-list-resolver
  [_env _props]
  {::pc/output [{:debug-menu/list [:debug-menu/id]}]}
  {:debug-menu/list
   (map
    (fn [id] [:debug-menu/id id])
    [:home
     :login
     :registration
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

(def resolvers
  [debug-menu-map-resolver
   debug-menu-resolver
   debug-menus-resolver])
