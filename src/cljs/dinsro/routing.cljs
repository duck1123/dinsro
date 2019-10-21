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
     ["/accounts"     :api-index-accounts]
     ["/rates"        :api-index-rates]
     ["/users"        :api-index-users]]]
   ["/currencies"
    [""               :index-currencies-page]
    ["/:id"           :show-currency-page]]
   ["/login"          :login-page]
   ["/rates"          :index-rates-page]
   ["/register"       :register-page]
   ["/settings"       :settings-page]
   ["/users"          :index-users-page]])

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
