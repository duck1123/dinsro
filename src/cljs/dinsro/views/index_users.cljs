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
  {:dispatch [::e.users/do-fetch-index]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-users-page)
  :start [::init-page]})

(defn page
  []
  (let [users @(rf/subscribe [::e.users/items])]
    [:section.section>div.container>div.content
     [:h1 (tr [:users-page "Users Page"])]
     [button/fetch-users]
     [index-users users]]))
