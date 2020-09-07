(ns dinsro.views.show-user
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.show-user :refer [show-user]]
   [dinsro.components.user-accounts :as c.user-accounts]
   [dinsro.components.user-categories :as c.user-categories]
   [dinsro.components.user-transactions :as c.user-transactions]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn init-page
  [_ [{:keys [id]}]]
  {:document/title "Show User"
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.categories/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.users/do-fetch-record id]]})

(s/fdef init-page
  :args (s/cat :cofx ::init-page-cofx
               :event ::init-page-event)
  :ret ::init-page-response)

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-user-page)
  :start  [::init-page]})

(defn load-buttons
  [store id]
  [:div.box
   [c.buttons/fetch-users store]
   [c.buttons/fetch-accounts store]
   [c.buttons/fetch-categories store]
   [c.buttons/fetch-currencies store]
   [c.buttons/fetch-transactions store]
   [c.buttons/fetch-user store id]])

(defn page-loaded
  [store id]
  (if-let [user @(st/subscribe store [::e.users/item id])]
    (let [user-id (:db/id user)]
      [:<>
       [:div.box
        [show-user store user]]
       [:<>
        (when-let [accounts @(st/subscribe store [::e.accounts/items-by-user user-id])]
          [c.user-accounts/section store user-id accounts])
        (when-let [categories @(st/subscribe store [::e.categories/items-by-user user-id])]
          [c.user-categories/section store user-id categories])
        (when-let [transactions @(st/subscribe store [::e.transactions/items-by-user user-id])]
          [c.user-transactions/section store user-id transactions])]])
    [:p "User not found"]))

(s/fdef page-loaded
  :args (s/cat :id pos-int?)
  :ret vector?)

(s/def :show-user-view/id          string?)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))

(defn page
  [store match]
  (let [{{:keys [id]} :path-params} match
        state @(st/subscribe store [::e.users/do-fetch-record-state])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store id])
     (condp = state
       :invalid [:p "invalid"]
       :failed [:p "Failed"]
       :loaded (page-loaded store (int id))
       [:p "unknown state"])]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match ::view-map
               ;; #(instance? rc/Match %)
               )
  :ret vector?)
