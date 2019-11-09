(ns dinsro.components.index-users
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [dinsro.views.show-user :as v.show-user]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::error-message (fn [db _] (get db ::error-message "")))

(defn-spec user-line any?
  [{:keys [db/id dinsro.model.user/name dinsro.model.user/email] :as user} ::e.users/item]
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
     (for [{:keys [db/id] :as user} users]
       ^{:key id} [user-line user]))))
