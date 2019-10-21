(ns dinsro.view
  (:require [dinsro.components.navbar :refer [navbar]]
            [dinsro.components.settings :as settings]
            [dinsro.views.about :as about]
            [dinsro.views.home :as home]
            [dinsro.views.index-accounts :as index-accounts]
            [dinsro.views.index-currencies :as index-currencies]
            [dinsro.views.index-rates :as index-rates]
            [dinsro.views.index-users :as index-users]
            [dinsro.views.login :as login]
            [dinsro.views.register :as register]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :about-page            about/page
    :home-page             home/page
    :index-accounts-page   index-accounts/page
    :index-currencies-page index-currencies/page
    :index-rates-page      index-rates/page
    :index-users-page      index-users/page
    :login-page            login/page
    :register-page         register/page
    :settings-page         settings/page
    nil                    [:div "Not Found"]]])
