(ns dinsro.views.admin
  (:require [dinsro.components :as c]
            [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
            [dinsro.components.admin-index-currencies :as c.admin-index-currencies]
            [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.index-users :as c.index-users]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Admin Page"
   :dispatch-n [[::e.accounts/do-fetch-index]
                [::e.users/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :admin-page)
  :start [::init-page]})

(defn load-buttons
  []
  [:div.box
   [c.buttons/fetch-users]
   [c.buttons/fetch-rate-sources]])

(defn users-section
  []
  [:div.box
   [:h2 (tr [:users])]
   (let [users @(rf/subscribe [::e.users/items])]
     [c.index-users/index-users users])])

(defn page
  []
  [:section.section>div.container>div.content
   (c.debug/hide [load-buttons])
   [:h1 "Admin"]
   (comment [c.admin-index-currencies/section])
   [c.admin-index-rate-sources/section]
   [c.admin-index-accounts/section]
   [users-section]])
