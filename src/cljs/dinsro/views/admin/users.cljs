(ns dinsro.views.admin.users
  (:require
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.index-users :refer [index-users]]
   [dinsro.events.users :as e.users]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

(defn init-page
  [_ _]
  {:dispatch [::e.users/do-fetch-index]
   :document/title "Index Users"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :admin-index-users-page)
  :start [::init-page]})

(defn load-buttons
  []
  [:div.box
   [c.buttons/fetch-users]])

(defn page
  [_store _match]
  (let [users @(rf/subscribe [::e.users/items])]
    [:section.section>div.container>div.content
     (c.debug/hide [load-buttons])
     [:div.box
      [:h1 (tr [:users-page "Admin Users Page"])]
      [:hr]
      [index-users users]]]))
