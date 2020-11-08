(ns dinsro.views.admin.users
  (:require
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.index-users :refer [index-users]]
   [kee-frame.core :as kf]
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
      [:h1 (tr [:users-page "Admin Users Page"])]
      [:hr]
      [index-users store users]]]))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-page :admin-index-users-page)
    :start [::init-page]})

  store)
