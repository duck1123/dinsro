(ns dinsro.views.registration
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
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
  [store _match]
  (let [allow-registration @(st/subscribe store [s.e.f.settings/allow-registration])]
    [:section.section>div.container>div.content
     (if allow-registration
       [:<>
        [:h1 "Registration Page"]
        [c.f.registration/form store]]
       [:div
        [:p "Registrations are not enabled"]])]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
