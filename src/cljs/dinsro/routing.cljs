(ns dinsro.routing
  (:require [re-frame.core :as rf]))

(def routes
  [["/"               :home-page]
   ["/about"          :about-page]
   ["/accounts"
    [""               :index-accounts-page]
    ["/:id"           :show-account-page]]
   ["/api"
    ["/v1"
     ["/authenticate" :api-authenticate]
     ["/accounts"
      [""             :api-index-accounts]
      ["/:id"         :api-show-account]]
     ["/currencies"
      [""             :api-index-currencies]
      ["/:id"         :api-show-currency]]
     ["/logout"       :api-logout]
     ["/rates"        :api-index-rates]
     ["/transactions"
      [""             :api-index-transactions]
      ["/:id"         :api-show-transaction]]
     ["/users"
      [""             :api-index-users]
      ["/:id"         :api-show-user]]]]
   ["/currencies"
    [""               :index-currencies-page]
    ["/:id"           :show-currency-page]]
   ["/login"          :login-page]
   ["/rates"          :index-rates-page]
   ["/register"       :register-page]
   ["/settings"       :settings-page]
   ["/users"
    [""               :index-users-page]
    ["/:id"           :show-user-page]]])

(rf/reg-sub
 :nav/route
 :<- [:kee-frame/route]
 identity)

(rf/reg-event-fx
 :nav/route-name
 (fn [_ [_ route-name]]
   {:navigate-to [route-name]}))

(rf/reg-sub
 :nav/page
 :<- [:nav/route]
 (fn [route _]
   (-> route :data :name)))
