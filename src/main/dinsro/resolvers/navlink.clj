(ns dinsro.resolvers.navlink
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.queries.settings :as q.settings]
   [dinsro.sample :as sample]
   [taoensso.timbre :as log]))

(defresolver navlink-resolver
  [_env {:navlink/keys [id]}]
  {::pc/input  #{:navlink/id}
   ::pc/output [:navlink/href
                :navlink/name]}
  (get sample/navlink-map id))

(defresolver navlinks-resolver
  [_env _props]
  {::pc/output [{:all-navlinks [:navlink/id]}]}
  {:all-navlinks (map (fn [id] [:navlink/id id]) (keys sample/navlink-map))})

(defresolver navlink-map-resolver
  [_env _props]
  {::pc/output [:navlink/map]}
  {:navlink/map sample/navlink-map})

(defresolver auth-link-resolver
  [_env _props]
  {::pc/output [{:auth-link [:navlink/id]}]}
  {:auth-link [:navlink/id "accounts"]})

(defresolver menu-links-resolver
  [_env _props]
  {::pc/output [{:menu-links [:navlink/id]}]}
  {:menu-links (map (fn [id] [:navlink/id id]) ["accounts"
                                                "transactions"])})

(defresolver unauth-links-resolver
  [_env _props]
  {::pc/output [{:unauth-links [:navlink/id]}]}
  (let [registration-enabled? (:allow-registration (q.settings/get-settings))]
    {:unauth-links
     (filter identity
             [(get sample/navlink-map "login")
              (when registration-enabled?
                (get sample/navlink-map "registration"))])}))

(defresolver dropdown-links-resolver
  [_env _props]
  {::pc/output [{:dropdown-links [:navlink/id]}]}
  {:dropdown-links
   (map (fn [id] [:navlink/id id])
        ["settings"
         "currencies"
         "admin"
         "rate-sources"
         "rates"
         "categories"
         "users"
         "transactions"
         "accounts"])})

(def resolvers
  [dropdown-links-resolver
   navlink-resolver
   navlinks-resolver
   navlink-map-resolver
   auth-link-resolver
   unauth-links-resolver
   menu-links-resolver])
