(ns dinsro.views.index-users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.filters :as u.filters]
   [dinsro.ui.index-users :refer [index-users]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.users/do-fetch-index]
   :document/title "Index Users"})

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-users store]])

(defn page
  [store _match]
  (let [users @(st/subscribe store [::e.users/items])]
    [:section.section>div.container>div.content
     (u.debug/hide store [load-buttons store])
     [:div.box
      [:h1 (tr [:users-page "Users Page"])]
      [:hr]
      [index-users store users]]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u.filters/filter-page :index-users-page)
    :start [::init-page]})

  store)
