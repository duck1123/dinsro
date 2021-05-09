(ns dinsro.resolvers.debug-menu
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [taoensso.timbre :as log]))

(def debug-menu-list
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
   :users])

(defresolver debug-menu-list-resolver
  [_env _props]
  {::pc/output [{:debug-menu/list [:navlink/id]}]}
  {:debug-menu/list (map (fn [id] [:navlink/id id]) debug-menu-list)})

(def resolvers
  [debug-menu-list-resolver])
