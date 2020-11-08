(ns dinsro.views.show-user
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.show-user :refer [show-user]]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-categories :as u.user-categories]
   [dinsro.ui.user-transactions :as u.user-transactions]
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

(defn load-buttons
  [store id]
  [:div.box
   [u.buttons/fetch-users store]
   [u.buttons/fetch-accounts store]
   [u.buttons/fetch-categories store]
   [u.buttons/fetch-currencies store]
   [u.buttons/fetch-transactions store]
   [u.buttons/fetch-user store id]])

(defn page-loaded
  [store id]
  (if-let [user @(st/subscribe store [::e.users/item id])]
    (let [user-id (:db/id user)]
      [:<>
       [:div.box
        [show-user store user]]
       [:<>
        (when-let [accounts @(st/subscribe store [::e.accounts/items-by-user user-id])]
          [u.user-accounts/section store user-id accounts])
        (when-let [categories @(st/subscribe store [::e.categories/items-by-user user-id])]
          [u.user-categories/section store user-id categories])
        (when-let [transactions @(st/subscribe store [::e.transactions/items-by-user user-id])]
          [u.user-transactions/section store user-id transactions])]])
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
     (u.debug/hide store [load-buttons store id])
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

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-param-page :show-user-page)
    :start  [::init-page]})

  store)
