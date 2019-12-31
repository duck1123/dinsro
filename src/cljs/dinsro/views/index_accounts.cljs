(ns dinsro.views.index-accounts
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-account :as c.f.create-account]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
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
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-users]]))

(defn-spec page vector?
  [match any?]
  (let [accounts @(rf/subscribe [::e.accounts/items])
        state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:section.section>div.container>div.content
     [loading-buttons]
     [:div.box
      [:h1
       (tr [:index-accounts])
       [c/show-form-button ::c.f.create-account/shown? ::c.f.create-account/set-shown?]]
      [c.f.create-account/form]
      [:hr]
      (condp = state
        :invalid [:p "Invalid"]
        :loaded  [index-accounts accounts]
        [:p "Unknown state: " state])]]))
