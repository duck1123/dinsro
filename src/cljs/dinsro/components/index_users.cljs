(ns dinsro.components.index-users
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.events.users :as e.users]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-user :as v.show-user]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(def default-error-message "")
(rf/reg-sub ::error-message (fn [db _] (get db ::error-message default-error-message)))

(defn user-link
  [user]
  (let [name (::s.users/name user)
        id (::s.users/id user)]
    [:a {:href (kf/path-for [:show-user-page {:id id}])} name]))

(defn delete-button
  [user]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.users/do-delete-record user])}
   (tr [:delete])])

(defn-spec user-line any?
  [user ::s.users/item]
  (let [id (:db/id user)
        email (::s.users/email user)]
    [:div.box
     [:p (tr [:id-label] [id])]
     [:p (tr [:name-label] [[user-link user]])]
     [:p (tr [:email-label] [email])]
     [delete-button user]]))

(defn index-users
  [users]
  (if-not (seq users)
    [:div [:p (tr [:no-users])]]
    (into [:div.section]
          (for [{:keys [db/id] :as user} users]
            ^{:key id} [user-line user]))))
