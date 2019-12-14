(ns dinsro.views.index-accounts
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as button]
            [dinsro.components.forms.create-account :refer [new-account-form]]
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

(kf/reg-event-fx
 ::init-page
 (fn-traced [{:keys [db]} _]
   {:dispatch [::e.accounts/do-fetch-index]}))

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) :index-accounts-page) true)
  :start [::init-page]})

(defn loading-buttons
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [button/fetch-accounts]
     [button/fetch-currencies]
     [button/fetch-users]]))

(defn-spec page vector?
  [match any?]
  (let [accounts @(rf/subscribe [::e.accounts/items])
        state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:section.section>div.container>div.content
     [:h1 (tr [:index-accounts])]
     [loading-buttons]
     [:div.box
      [new-account-form]
      [:hr]
      (condp = state
        :invalid [:p "Invalid"]
        :loaded  [index-accounts accounts]
        [:p "Unknown state: " state])]]))
