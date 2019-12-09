(ns dinsro.views.show-user
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-user-account :refer [add-user-account]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn-spec init-page ::init-page-response
  [cofx ::init-page-cofx event ::init-page-event]
  (let [[{:keys [id]}] event]
    {:dispatch [::e.users/do-fetch-record id]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-user-page)
  :start  [::init-page]})

(s/def :show-user-view/id          string?)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))

(defn load-box
  [id]
  [:div.box
   [c.buttons/fetch-users]
   [c.buttons/fetch-accounts]
   [c.buttons/fetch-currencies]
   (let [state @(rf/subscribe [::e.users/do-fetch-record-state])]
     [:button.button {:on-click #(rf/dispatch [::e.users/do-fetch-record id])}
      (str "Load User: " state)])])

(defn-spec page vector?
  [match ::view-map]
  (let [{{:keys [id]} :path-params} match
        state @(rf/subscribe [::e.users/do-fetch-record-state])]
    [:section.section>div.container>div.content
     #_[load-box id]
     (condp = state
       :invalid [:p "invalid"]
       :failed [:p "Failed"]
       :loaded (if-let [user @(rf/subscribe [::e.users/item (int id)])]
                 (let [user-id (:db/id user)
                       accounts @(rf/subscribe [::e.accounts/items-by-user user-id])]
                   [:<>
                    [:div.box
                     [:h1 "Show User"]
                     [show-user user accounts]]
                    [:div.box
                     [:h2 "Accounts"]
                     [add-user-account user-id]
                     [:hr]
                     [index-accounts accounts]]])
                 [:p "User not found"])
       [:p "unknown state"])]))
