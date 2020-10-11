(ns dinsro.views.admin
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
   [dinsro.components.admin-index-categories :as c.admin-index-categories]
   [dinsro.components.admin-index-currencies :as c.admin-index-currencies]
   [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
   [dinsro.components.admin-index-transactions :as c.admin-index-transactions]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.index-users :as c.index-users]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
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
   [c.buttons/fetch-accounts store]
   [c.buttons/fetch-categories store]
   [c.buttons/fetch-currencies store]
   [c.buttons/fetch-rate-sources store]
   [c.buttons/fetch-users store]])

(defn users-section
  [store]
  [:div.box
   [:h2 (tr [:users])]
   (let [users @(st/subscribe store [::e.users/items])]
     [c.index-users/index-users store users])])

(defn page
  [store _match]
  [:section.section>div.container>div.content
   (c.debug/hide store [load-buttons store])
   [:div.box
    [:h1.title "Admin"]]
   [c.admin-index-accounts/section store]
   [c.admin-index-transactions/section store]
   [c.admin-index-categories/section store]
   [c.admin-index-currencies/section store]
   [c.admin-index-rate-sources/section store]
   [users-section store]])

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
   {:params (c/filter-page :admin-page)
    :start [::init-page]})

  store)
