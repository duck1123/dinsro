(ns dinsro.views.index-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Index Accounts"
   :dispatch-n [[::e.accounts/do-fetch-index]
                [::e.users/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(s/fdef init-page
  :ret (s/keys))

(defn loading-buttons
  [store]
  [:div.box
   [u.buttons/fetch-accounts store]
   [u.buttons/fetch-currencies store]
   [u.buttons/fetch-users store]])

(s/fdef loading-buttons
  :ret vector?)

(defn page
  [store _match]
  (if-let [user-id @(st/subscribe store [:dinsro.events.authentication/auth-id])]
    [:section.section>div.container>div.content
     (u.debug/hide store [loading-buttons store])

     (let [state @(st/subscribe store [::e.accounts/do-fetch-index-state])]
       (condp = state
         :invalid
         [:p "Invalid"]

         :loaded
         (let [accounts @(st/subscribe store [::e.accounts/items-by-user user-id])]
           [u.user-accounts/section store user-id accounts])

         [:p "Unknown state: " state]))]
    [:p "Not Authenticated"]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-page :index-accounts-page)
    :start [::init-page]})

  store)
