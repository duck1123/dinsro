(ns dinsro.views.register
  (:require
   [dinsro.components :as c]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
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
  [_store _match]
  (let [allow-registration @(rf/subscribe [s.e.f.settings/allow-registration])]
    [:section.section>div.container>div.content
     (if allow-registration
       [:<>
        [:h1 "Registration Page"]
        [c.f.registration/form]]
       [:div
        [:p "Registrations are not enabled"]])]))
