(ns dinsro.views.show-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.components.user-accounts :as c.user-accounts]
            [dinsro.components.user-categories :as c.user-categories]
            #_[dinsro.components.user-transactions :as c.user-transactions]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.categories :as e.categories]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.users :as e.users]
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
    {:document/title "Show User"
     :dispatch-n [[::e.currencies/do-fetch-index]
                  [::e.categories/do-fetch-index]
                  [::e.accounts/do-fetch-index]
                  [::e.users/do-fetch-record id]]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-user-page)
  :start  [::init-page]})

(defn load-buttons
  [id]
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-users]
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-categories]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-user id]]))

(defn-spec page-loaded vector?
  [id pos-int?]
  (if-let [user @(rf/subscribe [::e.users/item (int id)])]
    (let [user-id (:db/id user)
          accounts @(rf/subscribe [::e.accounts/items-by-user user-id])
          categories @(rf/subscribe [::e.categories/items-by-user user-id])
          transactions @(rf/subscribe [::e.transactions/items-by-user user-id])]
      [:<>
       [:div.box
        [:h1 "Show User"]
        [show-user user]]
       [c.user-categories/section user-id categories]
       [c.user-accounts/section user-id accounts]
       #_[c.user-transactions/section user-id transactions]])
    [:p "User not found"]))

(s/def :show-user-view/id          string?)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))

(defn-spec page vector?
  [match ::view-map]
  (let [{{:keys [id]} :path-params} match
        state @(rf/subscribe [::e.users/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [load-buttons id]
     (condp = state
       :invalid [:p "invalid"]
       :failed [:p "Failed"]
       :loaded (page-loaded (int id))
       [:p "unknown state"])]))
