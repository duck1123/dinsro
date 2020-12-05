(ns dinsro.views.admin
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.admin-index-transactions :as u.admin-index-transactions]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.filters :as u.filters]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Admin Page"
   :dispatch-n [[::e.admin-accounts/do-fetch-index]
                [::e.categories/do-fetch-index]
                [::e.currencies/do-fetch-index]
                [::e.rate-sources/do-fetch-index]
                [::e.users/do-fetch-index]]})

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-accounts store]
   [u.buttons/fetch-categories store]
   [u.buttons/fetch-currencies store]
   [u.buttons/fetch-rate-sources store]
   [u.buttons/fetch-users store]])

(defn page
  [store _match]
  [:section.section>div.container>div.content
   (u.debug/hide store [load-buttons store])
   [:div.box
    [:h1.title "Admin"]]
   [u.admin-index-accounts/section store]
   [u.admin-index-transactions/section store]
   [u.admin-index-categories/section store]
   [u.admin-index-currencies/section store]
   [u.admin-index-rate-sources/section store]
   [u.admin-index-users/section store]])

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
   {:params (u.filters/filter-page :admin-page)
    :start [::init-page]})

  store)
