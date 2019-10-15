(ns dinsro.view
  (:require [dinsro.components.navbar :refer [navbar]]
            [dinsro.components.settings :as settings]
            [dinsro.components.index-users :as index-users]
            [dinsro.views.about :as about]
            [dinsro.views.home :as home]
            [dinsro.views.index-accounts :as index-accounts]
            [dinsro.views.login :as login]
            [dinsro.views.register :as register]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :about-page          about/page
    :home-page           home/page
    :index-accounts-page index-accounts/page
    :index-users-page    index-users/page
    :login-page          login/page
    :register-page       register/page
    :settings-page       settings/page
    nil                  [:div ""]]])
