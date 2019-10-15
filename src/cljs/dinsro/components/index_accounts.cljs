(ns dinsro.components.index-accounts
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::accounts (fn [db _] (get db ::accounts [])))

(kf/reg-event-fx
 ::do-delete-account-success
 (fn [_ _]
   (timbre/info "delete account success")))

(kf/reg-event-fx
 ::do-delete-account-failed
 (fn-traced [_ _]
   (timbre/info "delete account failed")))

(kf/reg-event-db
 ::do-fetch-accounts-success
 (fn-traced [db [_ {:keys [items]}]]
   (timbre/info "fetch accounts success" items)
   (assoc db ::accounts items)))

(kf/reg-event-fx
 ::do-fetch-accounts-failed
 (fn-traced [_ _]
   (timbre/info "fetch accounts failed")))

(kf/reg-event-fx
 ::do-fetch-accounts
 (fn-traced [_ _]
   {:http-xhrio
    {:uri             "/api/v1/accounts"
     :method          :get
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-accounts-success]
     :on-failure      [::do-fetch-accounts-failed]}}))

(kf/reg-event-fx
 ::do-delete-account
 (fn-traced [_ [_ id]]
   {:http-xhrio
    {:uri             (str "/api/v1/accounts/" id)
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-delete-account-success]
     :on-failure      [::do-delete-account-failed]}}))

(defn index-accounts
  []
  (let [accounts @(rf/subscribe [::accounts])]
    [:div
     [:a.button {:on-click #(rf/dispatch [::do-fetch-accounts])} "Load"]
     [:p "Index accounts"]
     (into
      [:div.section]
      (for [{:keys [id name] :as account} accounts]
        ^{:key (:id account)}
        [:div.column
         {:style {:border "1px black solid"
                  :margin-bottom "15px"}}
         [:p id]
         [:p name]
         [:a.button {:on-click #(rf/dispatch [::do-delete-account id])} "Delete"]]))]))
