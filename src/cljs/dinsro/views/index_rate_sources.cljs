(ns dinsro.views.index-rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-rate-source :as c.f.create-rate-source]
   [dinsro.components.index-rate-sources :as c.index-rate-sources]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.rate-sources/items [])
   :document/title "Index Rates Sources"
   :dispatch-n [
                [::e.currencies/do-fetch-index]
                [::e.rate-sources/do-fetch-index]
                ]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-rate-sources-page)
  :start [::init-page]})

(defn load-buttons
  [store]
  [:div.box
   [c.buttons/fetch-rate-sources store]
   [c.buttons/fetch-currencies store]])

(defn section
  [store items]
  [:div.box
   [:h1
    (tr [:rate-sources "Rate Sources"])
    [c/show-form-button store ::e.f.create-rate-source/shown?]]
   [c.f.create-rate-source/form store]
   [:hr]
   [c.index-rate-sources/section store items]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.rate-sources/items])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store])
     [section store items]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
