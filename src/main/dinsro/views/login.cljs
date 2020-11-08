(ns dinsro.views.login
  (:require
   [cemerick.url :as url]
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.forms.login :as u.f.login]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.f.login/set-defaults]
   :document/title "Login"})

(defn page
  [store match]
  (let [{:keys [query-string]} match
        return-to (get (url/query->map query-string) "return-to")]
    [:section.section>div.container>div.content
     [:h1 "Login"]
     [:div.container
      [u.f.login/form store return-to]]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page
   {:params (u/filter-page :login-page)
    :start [::init-page]})

  store)
