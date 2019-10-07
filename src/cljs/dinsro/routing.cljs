(ns dinsro.routing
  (:require [re-frame.core :as rf]))

(def routes
  [["/"              :home-page]
   ["/about"         :about-page]
   ["/accounts"      :index-accounts-page]
   ["/api/v1"
    ["/authenticate" :api-authenticate]
    ["/accounts"     :api-index-accounts]
    ["/users"        :api-index-users]]
   ["/login"         :login-page]
   ["/register"      :register-page]
   ["/settings"      :settings-page]
   ["/users"         :index-users-page]])

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
