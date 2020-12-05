(ns dinsro.routing)

(def api-routes
  [["/authenticate" :api-authenticate]
   ["/accounts"
    [""             :api-index-accounts]
    ["/:id"         :api-show-account]]
   ["/admin"
    ["/accounts"    :api-admin-index-accounts]
    ["/users"       :api-admin-index-users]]

   ["/categories"
    [""             :api-index-categories]
    ["/:id"         :api-show-category]]
   ["/currencies"
    [""             :api-index-currencies]
    ["/:id"
     [""            :api-show-currency]
     ["/rates"      :api-rate-feed]]]
   ["/logout"       :api-logout]
   ["/rate-sources"
    [""             :api-index-rate-sources]
    ["/:id"
     ["" :api-show-rate-source]
     ["/run" :api-run-rate-source]]]
   ["/rates"
    [""             :api-index-rates]
    ["/:id"         :api-show-rate]]
   ["/register"     :api-register]
   ["/settings"     :api-settings]
   ["/status"       :api-status]
   ["/transactions"
    [""             :api-index-transactions]
    ["/:id"         :api-show-transaction]]
   ["/users"
    [""             :api-index-users]
    ["/:id"         :api-show-user]]])

(def routes
  [["/"               :home-page]
   ["/about"          :about-page]
   ["/accounts"
    [""               :index-accounts-page]
    ["/:id"           :show-account-page]]
   (into ["/api/v1"] api-routes)
   ["/admin"
    [""               :admin-page]
    ["/users"         :admin-index-users-page]]
   ["/cards"          :cards-page]
   ["/categories"
    [""               :index-categories-page]
    ["/:id"           :show-category-page]]
   ["/currencies"
    [""               :index-currencies-page]
    ["/:id"           :show-currency-page]]
   ["/login"          :login-page]
   ["/rate-sources"
    [""               :index-rate-sources-page]
    ["/:id"           :show-rate-sources-page]]
   ["/rates"          :index-rates-page]
   ["/register"       :register-page]
   ["/transactions"   :index-transactions-page]
   ["/settings"       :settings-page]
   ["/users"
    [""               :index-users-page]
    ["/:id"           :show-user-page]]])
