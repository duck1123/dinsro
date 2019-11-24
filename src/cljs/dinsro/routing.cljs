(ns dinsro.routing
  (:require [dinsro.views.about :as about]
            [dinsro.views.home :as home]
            [dinsro.views.index-accounts :as index-accounts]
            [dinsro.views.index-currencies :as index-currencies]
            [dinsro.views.index-rates :as index-rates]
            [dinsro.views.index-users :as index-users]
            [dinsro.views.login :as login]
            [dinsro.views.register :as register]
            [dinsro.views.settings :as settings]
            [dinsro.views.show-account :as show-account]
            [dinsro.views.show-currency :as show-currency]
            [dinsro.views.show-user :as show-user]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]))

(def mappings
  {:about-page            about/page
   :home-page             home/page
   :index-accounts-page   index-accounts/page
   :index-currencies-page index-currencies/page
   :index-rates-page      index-rates/page
   :index-users-page      index-users/page
   :login-page            login/page
   :register-page         register/page
   :settings-page         settings/page
   :show-account-page     show-account/page
   :show-currency-page    show-currency/page
   :show-user-page        show-user/page})

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
     ["/rates"
      [""             :api-index-rates]
      ["/:id"         :api-show-rate]]
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

(kf/reg-event-fx
 :nav/route-name
 (fn [_ [route-name]]
   {:navigate-to [route-name]}))

(rf/reg-sub
 :nav/page
 :<- [:nav/route]
 (fn [route _]
   (-> route :data :name)))
