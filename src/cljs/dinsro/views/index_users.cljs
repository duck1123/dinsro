(ns dinsro.views.index-users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as button]
            [dinsro.components.index-users :refer [index-users]]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:dispatch [::e.users/do-fetch-index]
   :document/title "Index Users"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-users-page)
  :start [::init-page]})

(defn load-buttons
  []
  [:div.box
   [button/fetch-users]])

(defn page
  []
  (let [users @(rf/subscribe [::e.users/items])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1 (tr [:users-page "Users Page"])]
      [:hr]
      [index-users users]]]))
