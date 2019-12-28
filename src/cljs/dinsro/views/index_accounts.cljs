(ns dinsro.views.index-accounts
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.create-account :as c.f.create-account]
            [dinsro.components.index-accounts :as c.index-accounts]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.forms.create-account :as e.f.create-account]
            [dinsro.events.users :as e.users]
            [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Index Accounts"
   :dispatch-n [[::e.accounts/do-fetch-index]
                [::e.users/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-accounts-page)
  :start [::init-page]})

(defn loading-buttons
  []
  [:div.box
   [c.buttons/fetch-accounts]
   [c.buttons/fetch-currencies]
   [c.buttons/fetch-users]])

(defn page
  [_]
  (if-let [user-id @(rf/subscribe [:dinsro.events.authentication/auth-id])]
    (let [accounts @(rf/subscribe [::e.accounts/items-by-user user-id])
          state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
      [:section.section>div.container>div.content
       (c.debug/hide [loading-buttons])
       [:div.box
        [:h1
         (tr [:index-accounts])
         [c/show-form-button ::e.f.create-account/shown? ::e.f.create-account/set-shown?]]
        [c.f.create-account/form]
        [:hr]
        (condp = state
          :invalid [:p "Invalid"]
          :loaded  [c.index-accounts/index-accounts accounts]
          [:p "Unknown state: " state])]])))
