(ns dinsro.model.navlink
  (:refer-clojure :exclude [name])
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [taoensso.timbre :as log]))

(def auth-link-names ["accounts"])
(def dropdown-link-names
  ["users"
   "currencies"
   "categories"
   "rates"
   "rate-sources"
   "tx"
   "peers"
   "lightning-nodes"
   "core-nodes"
   "transactions"
   "accounts"
   "admin"])
(def menu-link-names ["accounts" "transactions"])
(def unauth-link-names ["login" "register"])

(defattr id ::id :string
  {ao/identity? true
   ao/schema    :production})

(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr href ::href :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr target ::target :keyword
  {ao/identities #{::id}
   ao/schema     :production})

(defattr all-navlinks ::all-navlinks :ref
  {ao/target    ::id
   ao/pc-output [{::all-navlinks [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     {::all-navlinks
      #?(:clj (queries/get-all-navlinks env query-params)
         :cljs [(comment env query-params)])})})

(defattr navbar-id :navbar/id :symbol
  {ao/identity? true})

(defattr auth-links ::auth-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::auth-links [::id]}]
   ao/pc-resolve
   (fn [env _]
     {::auth-links
      #?(:clj (queries/get-navlinks env auth-link-names)
         :cljs [(comment env)])})})

(defattr dropdown-links ::dropdown-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::dropdown-links [::id]}]
   ao/pc-resolve
   (fn [env _]
     {::dropdown-links
      #?(:clj  (queries/get-navlinks env dropdown-link-names)
         :cljs [(comment env)])})})

(defattr menu-links ::menu-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::menu-links [::id]}]
   ao/pc-resolve
   (fn [env _]
     {::menu-links
      #?(:clj (queries/get-navlinks env menu-link-names)
         :cljs [(comment env)])})})

(defattr unauth-links ::unauth-links :ref
  {ao/target     ::id
   ao/identities #{:navbar/id}
   ao/pc-output  [{::unauth-links [::id]}]
   ao/pc-resolve
   (fn [env _]
     {::unauth-links
      #?(:clj (queries/get-navlinks env unauth-link-names)
         :cljs [(comment env)])})})

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
  [id
   name
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

#?(:clj
   (def resolvers []))
