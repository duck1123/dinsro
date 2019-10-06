(ns dinsro.view
  (:require [dinsro.components.about :as about]
            [dinsro.components.accounts-page :as accounts]
            [dinsro.components.login-page :as login]
            [dinsro.components.navbar :refer [navbar]]
            [dinsro.components.register :as register]
            [dinsro.components.settings :as settings]
            [dinsro.components.index-users :as index-users]
            [dinsro.views.about :as about]
            [dinsro.views.accounts :as index-accounts]
            [dinsro.views.home :as home]
            [dinsro.views.login :as login]
            [dinsro.views.register :as register]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [re-frame.core :as rf]
            [reagent.core :as r]
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
