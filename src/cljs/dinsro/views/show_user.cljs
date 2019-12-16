(ns dinsro.views.show-user
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]

            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-user-account :as c.f.add-user-account]
            [dinsro.components.forms.add-user-category :as c.f.add-user-category]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.index-accounts :as c.index-accounts  :refer [index-accounts]]
            [dinsro.components.index-categories :as c.index-categories :refer [index-categories]]
            [dinsro.components.index-transactions :as c.index-transactions :refer [index-transactions]]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.components.user-accounts :as c.user-accounts]
            [dinsro.components.user-categories :as c.user-categories]
            [dinsro.components.user-transactions :as c.user-transactions]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.users :as e.users]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.transactions :as s.transactions]
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
    {:document/title "Show User"
     :dispatch [::e.users/do-fetch-record id]}))

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
          categories []
          transactions []]
      [:<>
       [:div.box
        [:h1 "Show User"]
        [show-user user]]
       [c.user-categories/section user-id categories]
       [c.user-accounts/section user-id accounts]
       [c.user-transactions/section user-id transactions]])
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
