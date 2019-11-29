(ns dinsro.views.index-users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.index-users :refer [index-users]]
            [dinsro.events.users :as e.users]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:dispatch [::e.users/do-fetch-index]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-users-page)
  :start [::init-page]})

(defn page
  []
  (let [users @(rf/subscribe [::e.users/items])
        state @(rf/subscribe [::e.users/do-fetch-index-state])]
    [:section.section>div.container>div.content
     [:h1 "Users Page"]
     [:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])}
      (str "Load Users: " state)]
     [index-users users]]))
