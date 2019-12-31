(ns dinsro.views.login
  (:require [cemerick.url :as url]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.login :as c.f.login]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:dispatch [::c.f.login/set-defaults]
   :document/title "Login"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page
 {:params (c/filter-page :login-page)
  :start [::init-page]})

(defn page [match]
  (let [{:keys [query-string]} match
        return-to (get (url/query->map query-string) "return-to")]
    [:section.section>div.container>div.content
     [:h1 "Login"]
     (c.debug/hide [:p "Authenticated: " @(rf/subscribe [::e.authentication/auth-id])])
     [:div.container
      (c.debug/hide [:p "Return To: " return-to])
      [c.f.login/form return-to]]]))
