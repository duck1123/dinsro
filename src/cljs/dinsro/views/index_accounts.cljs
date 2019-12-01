(ns dinsro.views.index-accounts
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.forms.account :refer [new-account-form]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
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

(defn-spec page vector?
  []
  (let [accounts @(rf/subscribe [::e.accounts/items])
        state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:section.section>div.container>div.content
     [:h1 "Index Accounts"]
     [:a.button {:on-click #(rf/dispatch [::init-page])}
      (str "Fetch Accounts: " state)]
     [:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
      (str "Fetch Currencies: " @(rf/subscribe [::e.currencies/do-fetch-index-state]))]
     [:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])}
      (str "Fetch Users: " @(rf/subscribe [::e.users/do-fetch-index-state]))]
     [new-account-form]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded  [index-accounts accounts]
       [:p "Unknown state: " state])]))
