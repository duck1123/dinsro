(ns dinsro.views.index-rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.create-rate-source :as u.f.create-rate-source]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.rate-sources/items [])
   :document/title "Index Rates Sources"
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.rate-sources/do-fetch-index]]})

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-rate-sources store]
   [u.buttons/fetch-currencies store]])

(defn section
  [store items]
  [:div.box
   [:h1
    (tr [:rate-sources "Rate Sources"])
    [u/show-form-button store ::e.f.create-rate-source/shown?]]
   [u.f.create-rate-source/form store]
   [:hr]
   [u.index-rate-sources/section store items]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.rate-sources/items])]
    [:section.section>div.container>div.content
     (u.debug/hide store [load-buttons store])
     [section store items]]))

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
   {:params (u/filter-page :index-rate-sources-page)
    :start [::init-page]})

  store)
