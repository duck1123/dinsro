(ns dinsro.views.index-users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.index-users :refer [index-users]]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.users/do-fetch-index]
   :document/title "Index Users"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-users-page)
  :start [::init-page]})

(defn load-buttons
  [store]
  [:div.box
   [c.buttons/fetch-users store]])

(defn page
  [store _match]
  (let [users @(st/subscribe store [::e.users/items])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store])
     [:div.box
      [:h1 (tr [:users-page "Users Page"])]
      [:hr]
      [index-users store users]]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
