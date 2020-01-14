(ns dinsro.views.index-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.user-accounts :as c.user-accounts]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
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

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-accounts-page)
  :start [::init-page]})

(defn loading-buttons
  []
  [:div.box
   [c.buttons/fetch-accounts]
   [c.buttons/fetch-currencies]
   [c.buttons/fetch-users]])

(s/fdef loading-buttons
  :ret vector?)

(defn page
  [_]
  (if-let [user-id @(rf/subscribe [:dinsro.events.authentication/auth-id])]
    [:section.section>div.container>div.content
     (c.debug/hide [loading-buttons])

     (let [state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
       (condp = state
         :invalid
         [:p "Invalid"]

         :loaded
         (let [accounts @(rf/subscribe [::e.accounts/items-by-user user-id])]
           [c.user-accounts/section user-id accounts])

         [:p "Unknown state: " state]))]
    [:p "Not Authenticated"]))

(s/fdef page
  :args (s/cat :match #(satisfies? rc/Match %))
  :ret vector?)
