(ns dinsro.components.index-users
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [dinsro.views.show-user :as v.show-user]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::error-message (fn [db _] (get db ::error-message "")))

(defn user-line
  [{:keys [id name email] :as user}]
  [:div.column
   {:style {:border "1px black solid"
            :margin-bottom "15px"}}
   [:p "Id: " id]
   [:p "Name: " [:a {:href (kf/path-for [::v.show-user/page user])} name]]
   [:p "Email " email]
   [:a.button {:on-click #(rf/dispatch [::e.users/do-delete-user user])} "Delete"]])

(defn index-users
  [users]
  (if-not (seq users)
    [:div [:p "No Users"]]
    (into
     [:div.section]
     (for [{:keys [id] :as user} users]
       ^{:key id} [user-line user]))))

(s/fdef index-users
  :args (s/cat :data ::ds/users))

(st/instrument 'index-users)
