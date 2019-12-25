(ns dinsro.views.admin
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.create-account :as c.f.create-account]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.components.index-users :as c.index-users]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.forms.create-account :as e.f.create-account]
            [dinsro.events.users :as e.users]
            [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
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
   [c.buttons/fetch-users]])

(defn users-section
  []
  [:div.box
   [:h2 "Users"]
   (let [users @(rf/subscribe [::e.users/items])]
     [c.index-users/index-users users])])

(defn page
  []
  [:section.section>div.container>div.content
   (c.debug/hide [load-buttons])
   [:h1 "Admin"]
   [users-section]])
