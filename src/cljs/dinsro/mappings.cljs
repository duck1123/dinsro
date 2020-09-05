(ns dinsro.mappings
  (:require
   [dinsro.views.about :as v.about]
   [dinsro.views.admin :as v.admin]
   [dinsro.views.admin.users :as v.a.users]
   [dinsro.views.home :as v.home]
   [dinsro.views.index-accounts :as v.index-accounts]
   [dinsro.views.index-categories :as v.index-categories]
   [dinsro.views.index-currencies :as v.index-currencies]
   [dinsro.views.index-rate-sources :as v.index-rate-sources]
   [dinsro.views.index-rates :as v.index-rates]
   [dinsro.views.index-transactions :as v.index-transactions]
   [dinsro.views.index-users :as v.index-users]
   [dinsro.views.login :as v.login]
   [dinsro.views.register :as v.register]
   [dinsro.views.settings :as v.settings]
   [dinsro.views.show-account :as v.show-account]
   [dinsro.views.show-currency :as v.show-currency]
   [dinsro.views.show-user :as v.show-user]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

(defn unknown-page
  []
  [:<>])

(def mappings
  {:about-page              v.about/page
   :admin-page              v.admin/page
   :admin-index-users-page  v.a.users/page
   :cards-page              unknown-page
   :home-page               v.home/page
   :index-accounts-page     v.index-accounts/page
   :index-categories-page   v.index-categories/page
   :index-currencies-page   v.index-currencies/page
   :index-rate-sources-page v.index-rate-sources/page
   :index-rates-page        v.index-rates/page
   :index-transactions-page v.index-transactions/page
   :index-users-page        v.index-users/page
   :login-page              v.login/page
   :register-page           v.register/page
   :settings-page           v.settings/page
   :show-account-page       v.show-account/page
   :show-currency-page      v.show-currency/page
   :show-user-page          v.show-user/page
   })

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
