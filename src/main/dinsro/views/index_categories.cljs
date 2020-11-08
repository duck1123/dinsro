(ns dinsro.views.index-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.ui.index-categories :refer [index-categories]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.categories/items [])
   :document/title "Index Categories"
   :dispatch-n [[::e.categories/do-fetch-index]
                [::e.users/do-fetch-index]]})

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-categories store]
   [u.buttons/fetch-currencies store]])

(defn page
  [store _match]
  (let [items @(st/subscribe store [::e.categories/items])]
    [:section.section>div.container>div.content
     (u.debug/hide store [load-buttons store])
     [:div.box
      [:h1
       (tr [:categories "Categories"])
       [u/show-form-button store ::e.f.create-category/shown?]]
      [u.f.create-category/form store]
      [:hr]
      (when items
        [index-categories store items])]]))

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
   {:params (u/filter-page :index-categories-page)
    :start [::init-page]})

  store)
