(ns dinsro.components.index-users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items (fn [db _] (get db ::items [])))

(kf/reg-event-db
 :index-users-failed
 (fn [db _]
   (assoc db :failed true)))

(kf/reg-event-db
 :index-users-loaded
 (fn [db [{users :users}]]
   (assoc db ::users users)))

(kf/reg-event-fx
 ::do-fetch-users
 (fn-traced [_ _]
  {:http-xhrio
   {:uri             "/api/v1/users"
    :method          :get
    :timeout         8000
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:index-users-loaded]
    :on-failure      [:index-users-failed]}}))

(kf/reg-event-fx
 :delete-user-success
 (fn [_ _]
   {}))

(kf/reg-event-fx
 :delete-user-failure
 (fn [_ _]
   {}))

(kf/reg-event-fx
 ::delete-user
 (fn [_ [{:keys [id] :as user}]]
   {:http-xhrio
    {:uri             (str "/api/v1/users/" id)
     :method          :delete
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:delete-user-success]
     :on-failure      [:delete-user-failure]}}))

(kf/reg-event-fx
 ::init-component
 (fn [{:keys [db]} _]
   {:db (-> db (assoc ::users []))
    :dispatch [::do-fetch-users]}))

(kf/reg-controller
 :index-users
 {:params (fn [{{:keys [name]} :data}]
            (= name ::page))
  :start [::init-component]})

(defn index-users
  [users]
  (if-let [users @(rf/subscribe [::users])]
    (into
     [:div.section]
     (for [{:keys [name email] :as user} users]
       ^{:key (:id user)}
       [:div.column
        {:style {:border "1px black solid"
                 :margin-bottom "15px"}}
        [:p "Name: " name]
        [:p "Email " email]
        [:a.button {:on-click #(rf/dispatch [::delete-user user])} "Delete"]]))
    [:div [:p "No Users"]]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Users Page"]
   [:a.button {:on-click #(rf/dispatch [::init-component])} "Reset"]
   [index-users]])
