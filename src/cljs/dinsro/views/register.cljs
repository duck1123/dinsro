(ns dinsro.views.register
  (:require [dinsro.components :as c]
            [dinsro.components.forms.registration :as c.f.registration]
            [dinsro.events.forms.registration :as e.f.registration]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.f.registration/set-defaults]
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
   [c.f.registration/form]])