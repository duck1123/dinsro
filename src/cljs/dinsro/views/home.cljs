(ns dinsro.views.home
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.account-picker :as c.account-picker]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.events.categories :as e.categories]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {
   ;; :db (assoc db ::e.categories/items [])
   :document/title "Home"
   :dispatch-n [
                [::e.currencies/do-fetch-index]
                ;; [::e.categories/do-fetch-index]
                ;; [::e.users/do-fetch-index]

                ]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :home-page)
  :start [::init-page]})

(defn page
  []
  (let [auth-id @(rf/subscribe [::e.authentication/auth-id])]
    [:section.section>div.container>div.content
     [:h1 (tr [:home-page])]
     (if auth-id
       [:<>
        [:div.box
         (str auth-id)]
        [c.account-picker/section]]
       [:div.box
        [:p "Not authenticated"]])]))
