(ns dinsro.views.index-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-category :as c.f.create-category]
   [dinsro.components.index-categories :refer [index-categories]]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.categories/items [])
   :document/title "Index Categories"
   :dispatch-n [[::e.categories/do-fetch-index]
                [::e.users/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-categories-page)
  :start [::init-page]})

(defn load-buttons
  [store]
  [:div.box
   [c.buttons/fetch-categories store]
   [c.buttons/fetch-currencies store]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.categories/items])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store])
     [:div.box
      [:h1
       (tr [:categories "Categories"])
       [c/show-form-button store ::e.f.create-category/shown?]]
      [c.f.create-category/form store]
      [:hr]
      (when items
        [index-categories store items])]]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
