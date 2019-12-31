(ns dinsro.views.register
  (:require [dinsro.components :as c]
            [dinsro.components.forms.registration-form :as c.f.registration-form]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::c.f.registration-form/set-defaults]
   :document/title "Registration"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page
 {:params (c/filter-page :register-page)
  :start [::init-page]})

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Registration Page"]
   [c.f.registration-form/registration-form]])
