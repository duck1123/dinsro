(ns dinsro.components.index-users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.events.users :as e.users]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::error-message (fn [db _] (get db ::error-message "")))

(defn index-users
  [users]
  (if (seq users)
    (into
     [:div.section]
     (for [{:keys [id name email] :as user} users]
       ^{:key (:id user)}
       [:div.column
        {:style {:border "1px black solid"
                 :margin-bottom "15px"}}
        [:p "Id: " id]
        [:p "Name: " name]
        [:p "Email " email]
        [:a.button {:on-click #(rf/dispatch [::do-delete-user user])} "Delete"]]))
    [:div [:p "No Users"]]))
