(ns dinsro.routing
  (:require
   [dinsro.views.about :as about]
   [dinsro.views.admin :as v.admin]
   [dinsro.views.home :as home]
   [dinsro.views.index-accounts :as index-accounts]
   [dinsro.views.index-categories :as v.index-categories]
   [dinsro.views.index-currencies :as index-currencies]
   [dinsro.views.index-rate-sources :as v.index-rate-sources]
   [dinsro.views.index-rates :as index-rates]
   [dinsro.views.index-transactions :as index-transactions]
   [dinsro.views.index-users :as index-users]
   [dinsro.views.login :as login]
   [dinsro.views.register :as register]
   [dinsro.views.settings :as settings]
   [dinsro.views.show-account :as show-account]
   [dinsro.views.show-currency :as show-currency]
   [dinsro.views.show-user :as show-user]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

(defn unknown-page
  []
  [:<>])

(def mappings
  {:about-page              about/page
   :admin-page              v.admin/page
   :cards-page              unknown-page
   :home-page               home/page
   :index-accounts-page     index-accounts/page
   :index-categories-page   v.index-categories/page
   :index-currencies-page   index-currencies/page
   :index-rate-sources-page v.index-rate-sources/page
   :index-rates-page        index-rates/page
   :index-transactions-page index-transactions/page
   :index-users-page        index-users/page
   :login-page              login/page
   :register-page           register/page
   :settings-page           settings/page
   :show-account-page       show-account/page
   :show-currency-page      show-currency/page
   :show-user-page          show-user/page
   })

(def api-routes
  [["/authenticate" :api-authenticate]
   ["/accounts"
    [""             :api-index-accounts]
    ["/:id"         :api-show-account]]
   ["/categories"
    [""             :api-index-categories]
    ["/:id"         :api-show-category]]
   ["/currencies"
    [""             :api-index-currencies]
    ["/:id"         :api-show-currency]]
   ["/logout"       :api-logout]
   ["/rate-sources"
    [""             :api-index-rate-sources]
    ["/:id"
     ["" :api-show-rate-source]
     ["/run" :api-run-rate-source]
     ]]
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
    ["" :admin-page]]
   ["/cards" :cards-page]
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

(comment
  routes

         )

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
